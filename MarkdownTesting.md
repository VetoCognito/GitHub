# Steps to remake RIP data


#### 1. Filter the database with user input PARK_ALHPA compared against objects ROUTE_IDENT

```
MakeQueryTable_management (in_table, out_table, in_key_field_option, {in_key_field}, {in_field}, {where_clause})
```
___
#### 2. Pull only necessary columns from the tables that are required

| MRL           | MRP           | PKG           | PMS_20        | PMS_MRL       | PMS_MRP       | PMS_PKG       | ROUTE_ID                | ROUTES        | 
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ----------------------- | ------------- |
| ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT   | ROUTE_IDENT             | ROUTE_IDENT   |
| RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE     | RIP_CYCLE_COLLECTED     | RIP_CYCLE     |
| RIP_ITERATION | RIP_ITERATION | RIP_ITERATION | RIP_ITERATION | RIP_ITERATION | RIP_ITERATION | RIP_ITERATION | RIP_ITERATION_COLLECTED | RIP_ITERATION |
| SHAPE         | SHAPE         | SHAPE         | BEG_MP        | BEG_MP        | PCR           | PCR           | ROUTE_NAME              | SHAPE         |
| -             | -             | -             | END_MP        | END_MP        | INSP_DATE     | INSP_DATE     | FROM_DESC               | SOURCE        |
| -             | -             | -             | NO_LANES      | NO_LANES      | -             | -             | TO_DESC                 | -             |
| -             | -             | -             | LANE_WIDTH    | LANE_WIDTH    | -             | -             | INSP_DATE               | -             |
| -             | -             | -             | PAVE_WIDTH    | PAVE_WIDTH    | -             | -             | FUNCT_CLASS             | -             |
| -             | -             | -             | SURF_TYPE     | SURF_TYPE     | -             | -             | USER_ACCESS             | -             |
| -             | -             | -             | PCR           | PCR           | -             | -             | FMSS_NO                 | -             |
| -             | -             | -             | RCI           | RCI           | -             | -             | M_DISTRICT              | -             |
| -             | -             | -             | SCR           | SCR           | -             | -             | COLLECTION_METHOD       | -             |
| -             | -             | -             | RUT_INDEX     | RUT_INDEX     | -             | -             | CONCESSION              | -             |
| -             | -             | -             | SC_INDEX      | SC_INDEX      | -             | -             | RTE_LENGTH              | -             |
| -             | -             | -             | AC_INDEX      | AC_INDEX      | -             | -             | SURF_TYPE               | -             |
| -             | -             | -             | LC_INDEX      | LC_INDEX      | -             | -             | SQ_FEET                 | -             |
| -             | -             | -             | TC_INDEX      | TC_INDEX      | -             | -             | SUMMARY_REC             | -             |
| -             | -             | -             | PATCH_INDEX   | PATCH_INDEX   | -             | -             | FLTP                    | -             |
| -             | -             | -             | INSP_DATE     | INSP_DATE     | -             | -             | -                       | -             |
     
```
AddField_management (in_table, field_name, field_type, {field_precision}, {field_scale}, {field_length}, {field_alias}, {field_is_nullable}, {field_is_required}, {field_domain})
```
___
#### 3. Join tables

```
AddJoin_management (in_layer_or_view, in_field, join_table, join_field, {join_type})
```
Routes:  
```
Roads == ([ROUTES] JOIN [MRL]) JOIN [ROUTE_ID]
Parking == [PKG] LEFT JOIN [ROUTE_ID]  
Roads (Polygons) == [MRP] LEFT JOIN [ROUTE_ID]  
```
Route Conditions:  
```
Road Conditions == ([ROUTES] JOIN [MRL]) JOIN ([PMS_20] RIGHT JOIN [PMS_MRL])  
Parking Conditions == [PKG] LEFT JOIN [PMS_PARKING]  
Roads Conditions (Polygons) == [MRP] LEFT JOIN [PMS_MRP]  
```
___
#### 4. Rename if necessary

```
AlterField_management (in_table, field, {new_field_name}, {new_field_alias}, {field_type}, {field_length}, {field_is_nullable}, {clear_field_alias})
```
___
#### 5. Copy to gdb

```
Copy_management (in_data, out_data, {data_type}, {associated_data})
```
___
