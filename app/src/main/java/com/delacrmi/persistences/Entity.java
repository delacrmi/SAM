package com.delacrmi.persistences;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private List<EntityColumn> columns = new ArrayList<EntityColumn>();
    private Map<String,Integer> hashcolumns = new HashMap<String,Integer>();
    private ContentValues columnValueList = new ContentValues();
    private boolean synchronizable = false;
    private EntityFilter entityFilter;

    public abstract Entity entityConfig();

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isSynchronizable() {
        return synchronizable;
    }

    public void setSynchronizable(boolean synchronizable) {
        this.synchronizable = synchronizable;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public void setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
    }

    public void configureEntityFilter(Context context){}

    public String getCreateString(){
        int count = 1;
        String create = "create table "+entityName+"(";

        for (EntityColumn column: columns){
            create += column.getCreateString();
            if(count < columns.size()){
                create  += ",";
                count++;
            }
        }
        create += ")";
        return create;
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

    public void addColumn(String name,EntityColumn.ColumnType type){
        columnList.put(name, type.toString().toLowerCase());
        columns.add(new EntityColumn(name,type));
    }

    public void addColumn(EntityColumn column){
        columnList.put(column.getName(),column.getType().toString().toLowerCase());
        columns.add(column);
        hashcolumns.put(column.getName(),columns.size()-1);
    }

    public void addColumns(ContentValues columns){
        columnList = columns;
    }

    public ContentValues getColumns(){
        return columnList;
    }

    public String getColumnsNameAsString(boolean primaryKey){
        int count = 1;
        /*Map.Entry me;*/
        String columns = "";

        //getting the iterator columns to get the key values
        /*Iterator iteratorColumns = iterator();
        while (iteratorColumns.hasNext()){
            me = (Map.Entry)iteratorColumns.next();

            if(!getPrimaryKey().equals(me.getKey().toString())) {

                columns += me.getKey();

                if(count < getColumnsCount()-1){
                    columns += ",";
                    count++;
                }

            }else if(primaryKey) {

                columns += getPrimaryKey();

                if(count < getColumnsCount()-1){
                    columns += ",";
                    count++;
                }

            }

        }*/

        for (EntityColumn column: this.columns){
            if(column.isPrimaryKey() == primaryKey){
                columns += column.getName();
                if(count < this.columns.size()-1){
                    columns += ",";
                    count++;
                }
            }
        }

        return columns;
    }

    public JSONObject getJSON(){

        JSONObject json = new JSONObject();

        for(EntityColumn column : columns)
            try {
                json.put(column.getName(),column.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return json;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();

        for(EntityColumn column : columns)
            if(column.getValue() == null)
                cv.putNull(column.getName());
            else if(column.getType().equals(
                    EntityColumn.ColumnType.DATE.toString().toLowerCase())){
                cv.put(column.getName(),((java.util.Date)column.getValue()).getTime());
            }else
                addValuesByType(cv,column.getName(),column.getValue(),column.getType().toString().toLowerCase());

        return cv;
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
        if(pk.equals("")){
            this.pk = entityName+"_id";
            columnList.put(pk,"integer");
        }
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



    @Deprecated
    public void setValue(String columnName,String value){
        if(columnList.containsKey(columnName))
            addValuesByType(columnValueList,columnName,value,columnList.getAsString(columnName));
    }

    public void setValue(String column,Object value){
        if(hashcolumns.containsKey(column))
            columns.get(hashcolumns.get(column)).setValue(value);
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

    private void addValuesByType(EntityColumn content,Object value){
        if ( value == null ){

        }else if (content.getType().equals("integer")) {
            if(!value.getClass().getSimpleName().equals(content.getType()))
                content.setValue(Integer.parseInt((String) value));
            else
                content.setValue((Integer) value);
        }else if (content.getType().equals("text")) {
            content.setValue(value.toString());
        }else if (content.getType().equals("real")) {
            if(!value.getClass().getSimpleName().equals(content.getType()))
                content.setValue(Float.parseFloat((String) value));
            else
                content.setValue((Float) value);
        }else if (content.getType().equals("date")) {
            content.setValue((java.util.Date)value);
        }
    }


}
