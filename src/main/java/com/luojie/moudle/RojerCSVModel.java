package com.luojie.moudle;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RojerCSVModel {

    /**
     *  @CsvBindByName注解是根据 CSV 文件中的列名进行绑定
     *  @CsvBindByPosition根据 CSV 文件中的位置（在哪一列）进行绑定
     */
//    @CsvBindByName(column = "Name", required = false)
    @CsvBindByPosition(position = 0, required = false)
    private String name;

//    @CsvBindByName(column = "Age", required = false)
    @CsvBindByPosition(position = 1, required = false)
    private String age;

//    @CsvBindByName(column = "Address", required = false)
    @CsvBindByPosition(position = 2, required = false)
    private String address;

    @Override
    public String toString() {
        return   name  +',' +  age +  ','  + address;
    }
}
