package com.voltor.ui.tabs.selling;

import java.util.Collection;

import com.voltor.util.UIProductModelsUtils;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.voltor.bean.Category;
import com.voltor.bean.Product;
import com.voltor.bean.Seller;
import com.voltor.bean.SellingPosition;
import com.voltor.bean.SubCategory;
import com.voltor.services.CategoryService;
import com.voltor.services.ProductService;
import com.voltor.services.SubCategoryService;
import com.voltor.util.UIComponentUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

@Component
public class ProductForSellingModel {

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SubCategoryService subCategoryService; 
	@Autowired
	private UIComponentUtils componentUtils;

	private Product editedValue;

	private TableView<Product> table;
	private ComboBox<Category> category;
	private ComboBox<SubCategory> subCategory;
	private TextField name;
	private TextField shtrihCode;
	private TextField code;
	private TextField count;
	private TextField price;
	private Seller seller;
	
	
	private boolean isWriting;
	
	 private Collection<Product> masterData = null;
	 private ObservableList<Product> filteredData = FXCollections.observableArrayList();

	public void init() {
		masterData = productService.getAllForSelling() ;
		editedValue = new Product();
		this.seller = null;
		initViewComponents();
		initListenersComponent();
	}

	private void initViewComponents() {
		createTable();
		ObservableList<Category> categoryData = FXCollections.observableArrayList(categoryService.getAll());
		category.setItems(categoryData);
		category.valueProperty().addListener(new ChangeListener<Category>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue observableValue, Category oldValue, Category newValue) {
				subCategory.getItems().clear();
				ObservableList<SubCategory> subCategoryData = null;
				if (newValue != null) {
					subCategoryData = FXCollections.observableArrayList(subCategoryService.getAllByCategory(newValue));
					subCategory.setItems(subCategoryData);
				} 
			}
		});
		updateFilteredData();
	}
	
	private void initListenersComponent(){
		componentUtils.addValueChangeListenerToComboBox(category, this::updateFilteredData);
		componentUtils.addValueChangeListenerToComboBox(subCategory, this::updateFilteredData);
		componentUtils.addValueChangeListenerToTextField(name, this::updateFilteredData);
		componentUtils.addValueChangeListenerToTextField(shtrihCode, this::updateFilteredData);
		componentUtils.addValueChangeListenerToTextField(code, this::updateFilteredData);
	}

	private void updateFilteredData() {
		if(isWriting){
			return;
		}
        filteredData.clear();

        for (Product p : masterData) {
        	if( seller !=null ){
        		p.setPriceType( seller.getPriceType() );
        	} else {
        		p.setPriceType( null );
        	}
            if (UIProductModelsUtils.matchesFilter(p,table,category,subCategory,name,shtrihCode,code)) {
                filteredData.add(p);
            }
        }
        updateTable();
    }


	private void createTable() {
		componentUtils.addTableColumn(table, "група", 172.0, Product.class, "category");
		componentUtils.addTableColumn(table, "бренд", 172.0, Product.class, "subCategory");
		componentUtils.addTableColumn(table, "назва", 172.0, Product.class, "name");
		componentUtils.addTableColumn(table, "код товару", 172.0, Product.class, "code");
		componentUtils.addTableColumn(table, "штрих код", 172.0, Product.class, "shtrihCode");
		componentUtils.addTableColumn(table, "ціна", 100.0, Product.class, "priceByType");
		componentUtils.addSelectionEventToTable(table, Product.class, e -> {
			editedValue = e;
			writeFields();
		});
	}

	public void updateTable() {
		table.setItems( filteredData );
		editedValue = new Product();
	}

	void clearFields() {
		category.setValue(null);
		subCategory.setValue(null);
		name.clear();
		code.clear();
		shtrihCode.clear();
		price.clear();
		count.clear();
		editedValue = new Product();
	}

	private void writeFields() {
		isWriting=true;
		category.getSelectionModel().select(editedValue.getCategory());
		subCategory.getSelectionModel().select(editedValue.getSubCategory());
		shtrihCode.setText(editedValue.getShtrihCode());
		name.setText(editedValue.getName());
		code.setText(editedValue.getCode());
		if( seller !=null ){
			editedValue.setPriceType( seller.getPriceType() );
    	}
		if( editedValue.getPriceByType() != null ){
			price.setText(editedValue.getPriceByType().toString());
		} else {
			price.clear();
		}
		isWriting=false;
	}

	boolean isValidateFields() {
		UIProductModelsUtils.isValidateFields(editedValue,componentUtils,price,count);
		if (!Strings.isNullOrEmpty(count.getText())) {
				Integer t = Integer.valueOf( count.getText().trim() );
				if( t > productService.getCount(editedValue)){
					componentUtils.showMessage("Наскладі тільки "+productService.getCount(editedValue));
					return false;
				}
			}
		return true;
	}

	void setTable(TableView<Product> table) {
		this.table = table;
	}

	void setSubCategory(ComboBox<SubCategory> subCategory) {
		this.subCategory = subCategory;
	}
	void setName(TextField name) {
		this.name = name;
	}

	void setShtrihCode(TextField shtrihCode) {
		this.shtrihCode = shtrihCode;
	}
	void setCode(TextField code) {
		this.code = code;
	}
	void setCategory(ComboBox<Category> category) {
		this.category = category;
	}

	public void setCount(TextField count) {
		this.count = count;
	}

	public void setPrice(TextField price) {
		this.price = price;
	}
	
	public SellingPosition getSellingPostition(){
		SellingPosition sellingPostition = new SellingPosition();
		sellingPostition.setProduct( editedValue );
		sellingPostition.setPrice( Double.valueOf( price.getText() ));
		sellingPostition.setCount( Integer.valueOf( count.getText() ));
		sellingPostition.calculateSum();
		return sellingPostition;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
		updateFilteredData();
	}
}
