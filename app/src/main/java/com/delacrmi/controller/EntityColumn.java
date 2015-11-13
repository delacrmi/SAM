package com.delacrmi.controller;

/**
 * Created by miguel on 13/11/15.
 */
public class EntityColumn<valueDefault> {

    private String name;
    private ColumnType type;
    private boolean isNullable = true;
    private boolean isPrimaryKey = false;
    private boolean autoIncrement = false;
    private valueDefault defaultValue;
    private boolean dbColumn = true;

    public EntityColumn(){}

    public EntityColumn(String name,ColumnType columnType){
        this.name = name;
        type = columnType;
    }

    public EntityColumn(String name,ColumnType columnType, valueDefault defaultValue){
        this.name = name;
        type = columnType;
        this.defaultValue = defaultValue;
    }

    public EntityColumn(String name,ColumnType columnType, boolean isPrimaryKey){
        this.name = name;
        type = columnType;
        this.isPrimaryKey = isPrimaryKey;
    }

    public EntityColumn(String name, boolean isPrimaryKey){
        this.name = name;
        this.isPrimaryKey = isPrimaryKey;
        type = ColumnType.INTEGER;
    }

    public EntityColumn(String name,ColumnType columnType,boolean isPrimaryKey,
                        boolean autoIncrement, valueDefault defaultValue){
        this.name = name;
        type = columnType;
        this.isPrimaryKey = isPrimaryKey;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
        isNullable = false;
    }

    public EntityColumn(String name,boolean isPrimaryKey,boolean autoIncrement){
        this.name = name;
        this.isPrimaryKey = isPrimaryKey;
        this.autoIncrement = autoIncrement;
        isNullable = false;
    }

    public boolean isNullable() {
        return isNullable;
    }
    public EntityColumn setNotNullable() {
        if(isPrimaryKey)
            throw new IllegalStateException("Can't set the nullable option if this column is primary key");
        this.isNullable = false;
        return this;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public EntityColumn setPrimaryKey() {
        isNullable = false;
        this.isPrimaryKey = true;
        return this;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    public EntityColumn setAutoIncrement() {
        this.autoIncrement = true;
        return this;
    }

    public valueDefault getDefaultValue(){
        return defaultValue;
    }
    public EntityColumn setDefaultValue(valueDefault defaultValue){
        this.defaultValue = defaultValue;
        return this;
    }

    public String getType() {
        return type.toString();
    }
    public EntityColumn setType(ColumnType type){
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }
    public EntityColumn setName(String name){
        this.name = name;
        return this;
    }

    public boolean isDbColumn() {
        return dbColumn;
    }
    public EntityColumn setOutDbColumn() {
        this.dbColumn = true;
        return this;
    }

    public enum ColumnType{
        TEXT,INTEGER,REAL,LONG,DATE
    }
}
