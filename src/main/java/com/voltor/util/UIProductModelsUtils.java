package com.voltor.util;

import com.voltor.bean.Category;
import com.voltor.bean.Product;
import com.voltor.bean.SubCategory;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.assertj.core.util.Strings;

/**
 * Created by Petro on 04.04.2017.
 */
public class UIProductModelsUtils {
    public static boolean matchesFilter(Product p, TableView<Product> table,
                                        ComboBox<Category> category,
                                        ComboBox<SubCategory> subCategory,
                                        TextField name,
                                        TextField shtrihCode,
                                        TextField code) {
        if(category.getValue()!=null){
            if(!p.getCategory().equals(category.getValue())){
                return false;
            }
        }
        if(subCategory.getValue()!=null){
            if(!p.getSubCategory().equals(subCategory.getValue())){
                return false;
            }
        }
        if( !Strings.isNullOrEmpty(name.getText()) ){
            if(!p.getName().toLowerCase().contains(name.getText().toLowerCase())){
                return false;
            }
        }
        if( !Strings.isNullOrEmpty(code.getText())){
            if( Strings.isNullOrEmpty(p.getCode()) || !p.getCode().toLowerCase().contains(code.getText().toLowerCase())){
                return false;
            }
        }
        if( !Strings.isNullOrEmpty(shtrihCode.getText()) ){
            if( Strings.isNullOrEmpty(p.getShtrihCode()) || !p.getShtrihCode().toLowerCase().contains(shtrihCode.getText().toLowerCase())){
                return false;
            }
        }
        return true;
    }

    public static boolean isValidateFields(Product editedValue,
                                           UIComponentUtils componentUtils,
                                           TextField price,
                                           TextField count) {
        if (editedValue == null || editedValue.getId()==0L) {
            componentUtils.showMessage("Будь-ласка, виберіть товар!");
            return false;
        }
        if (!Strings.isNullOrEmpty(price.getText())) {
            try {
                Double.parseDouble(price.getText().trim());
            } catch (NumberFormatException e) {
                componentUtils.showMessage("Ви неправельно ввели ціну! Приклад 1253.25");
                return false;
            }
        } else {
            componentUtils.showMessage("Ви не ввели ціну!");
            return false;
        }

        if (!Strings.isNullOrEmpty(count.getText())) {
            try {
                Integer.valueOf(count.getText().trim());
            } catch (NumberFormatException e) {
                componentUtils.showMessage("Ви неправельно ввели кількість! Приклад 55");
                return false;
            }
        } else {
            componentUtils.showMessage("Ви не ввели кількість!");
            return false;
        }
        return true;
    }
}
