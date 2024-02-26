package com.itletian.dto;

import com.itletian.entity.Setmeal;
import com.itletian.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
