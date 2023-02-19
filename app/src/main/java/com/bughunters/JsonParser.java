package com.bughunters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JsonParser {
 private HashMap<String,String> parseJasonObject(JSONObject object)  {
  HashMap<String,String> datalist =new HashMap<>();
  try {
   //get name from object
   String name = object.getString("name");

   //get latitude from object
   String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");

   //get longitude from object
   String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");

   //put all values in HashMap
   datalist.put("name",name);
   datalist.put("lat",latitude);
   datalist.put("lng",longitude);
  } catch (JSONException e) {
   e.printStackTrace();
  }
  //return hashmap
  return datalist;

 }
 private List <HashMap<String,String>> parseJsonArray(JSONArray jsonArray){

  //initialize hash map list
  List<HashMap<String,String>> datalist =new ArrayList<>();
  for(int i=0;i<jsonArray.length();i++){
   try {
    //initialize hashmap
    HashMap<String,String> data =parseJasonObject((JSONObject)jsonArray.get(i));
    //add data in hashmap  list
    datalist.add(data);
   } catch (JSONException e) {
    e.printStackTrace();
   }
  }
  //return hashmap list
  return datalist;
 }

 public List<HashMap<String,String>> parseResult(JSONObject object){
  //initialize json array
  JSONArray jsonArray=null;

  try {
   //get result array
   jsonArray=object.getJSONArray("results");
  } catch (JSONException e) {
   e.printStackTrace();
  }
  //return array
 return parseJsonArray(Objects.requireNonNull(jsonArray));
 }
}
