package com.delacrmi.controller;

import android.content.ContentValues;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by miguel on 09/10/15.
 */
public abstract class Entity implements Serializable{

    private String entityName = "";
    private String nickName = "";
    private String pk = "";
    private HashMap<String,ContentValues> constraint = new HashMap<String,ContentValues>();
    private ContentValues constraintDetails;
    private ContentValues columnList = new ContentValues();
    private ContentValues columnValueList = new ContentValues();

    public abstract Entity entityConfig();

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPrimaryKey(String columnName){
        if(!columnName.equals("") || !columnName.equals(" ")) {
            pk = columnName;
            columnList.remove(getName()+"_id");
            columnList.put(pk,"integer");
        }
    }

    public  String getPrimaryKey(){
        return pk;
    }

    public void setDefault(String columnName,String value,String type){
        haveDetailsColumn(columnName);
        constraintDetails.putNull("default");
        constraintDetails.put("value", value);
        constraintDetails.put("type", type);
    }

    public void setNullable(String columnName,boolean nullable){
        haveDetailsColumn(columnName);
        constraintDetails.put("nullable", nullable);
    }

    public HashMap<String,ContentValues> getColumnsConstraints(){
        return constraint;
    }

    public ContentValues getColumnConstraints(){
        return constraint.get(getName());
    }

    public void addColumn(String name,String type){
        columnList.put(name,type);
    }

    public void addColumns(ContentValues columns){
        columnList = columns;
    }

    public ContentValues getColumns(){
        return columnList;
    }

    public JSONArray getColumnstoJSONArray(){
        JSONArray array = new JSONArray();

        Iterator iterator = iterator();
        while (iterator.hasNext()){
            array.put(((Map.Entry)iterator.next()).getKey());
        }

        return array;
    }

    public Iterator iterator(){
        return  getColumns().valueSet().iterator();
    }

    public int getColumnsCount(){
        return getColumns().size();
    }

    public String getName(){
        if(entityName.equals(""))
            return getClass().getSimpleName();

        return entityName;
    }

    public Entity setName(String entityName){
        this.entityName = entityName;
        this.pk = entityName+"_id";
        columnList.put(pk,"integer");
        return this;
    }

    private void haveDetailsColumn(String columnName) {
        if(!columnList.containsKey(columnName))
            return;

        if(constraint.containsKey(columnName))
            constraintDetails = constraint.get(columnName);
        else
            constraintDetails = new ContentValues(5);
    }

    public ContentValues getColumnValueList(){
        return columnValueList;
    }

    public void setValues(ContentValues args) {
        Iterator iterator = args.valueSet().iterator();
        while (iterator.hasNext()){
            Map.Entry me= (Map.Entry)iterator.next();
            if(columnList.containsKey(me.getKey().toString())){
                addValuesByType(columnValueList,me.getKey().toString(),
                        args.get(me.getKey().toString()),columnList.getAsString(me.getKey().toString()));
            }
        }
    }

    public void setValue(String columnName,String value){
        if(columnList.containsKey(columnName))
            addValuesByType(columnValueList,columnName,value,columnList.getAsString(columnName));
    }

    private void addValuesByType(ContentValues content,String name,Object value,String type) {
        if ( value == null )
            content.putNull(name);
        else if (type.equals("integer")) {
            if(!value.getClass().getSimpleName().equals(type))
                content.put(name,Integer.parseInt((String)value));
            else
                content.put(name,(Integer)value);
        }else if (type.equals("text")) {
            content.put(name,value.toString());
        }else if (type.equals("real")) {
            if(!value.getClass().getSimpleName().equals(type))
                content.put(name,Float.parseFloat((String)value));
            else
                content.put(name,(Float)value);
        }else if (type.equals("date")) {
            if(!value.getClass().getSimpleName().equals(type))
                content.put(name,Long.parseLong((String)value));
            else
                content.put(name,(Long)value);
        }
    }
}
