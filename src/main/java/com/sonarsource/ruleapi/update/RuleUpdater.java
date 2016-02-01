/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.update;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.Fetcher;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RuleUpdater {

  private static final Logger LOGGER = Logger.getLogger(RuleUpdater.class.getName());


  private RuleUpdater() {
    // hide utility class instantiation
  }


  public static boolean updateRule(String ruleKey, Map<String,Object> fieldValuesToUpdate, String login, String password) {

    if (!ruleKey.matches("RSPEC-[0-9]+") || fieldValuesToUpdate.isEmpty()) {
      return false;
    }

    Fetcher fetcher = new Fetcher();
    JSONObject jobj = fetcher.getJsonFromUrl(Fetcher.BASE_URL + Fetcher.ISSUE + ruleKey + "/editmeta", login, password);
    JSONObject fieldsMeta = (JSONObject)jobj.get("fields");

    JSONObject request = prepareRequest(fieldValuesToUpdate, fieldsMeta);
    LOGGER.fine("Update " + ruleKey + " : " + request.toJSONString());

    Updater updater = new Updater();
    return updater.putIssueUpdate(login, password, ruleKey, request);
  }

  public static boolean updateRuleStatus(String ruleKey, Rule.Status status, String login, String password) {

    Fetcher fetcher = new Fetcher();
    JSONObject jobj = fetcher.getJsonFromUrl(Fetcher.BASE_URL + Fetcher.ISSUE + ruleKey + "/transitions?expand=transitions.fields", login, password);

    JSONObject request = prepareTransitionRequest(status, jobj);
    LOGGER.fine("Update " + ruleKey + " : " + request.toJSONString());

    Updater updater = new Updater();
    return updater.postIssueUpdate(login, password, ruleKey + "/transitions", request);
  }

  protected static JSONObject prepareTransitionRequest(Rule.Status status, JSONObject jobj) {

    List<JSONObject> transitions = (JSONArray)jobj.get("transitions");

    JSONObject request = new JSONObject();
    JSONObject transition = new JSONObject();
    request.put("transition", transition);

    for (JSONObject tmp : transitions) {
      String name = ((JSONObject) tmp.get("to")).get("name").toString();
      if (Rule.Status.fromString(name).equals(status)) {
        transition.put("id", tmp.get("id"));
        break;
      }
    }
    return request;
  }

  protected static JSONObject prepareRequest(Map<String, Object> fieldValuesToUpdate, JSONObject fieldsMeta) {

    Map<String,String> fieldIds = extractFieldIds(fieldsMeta);

    JSONObject request = new JSONObject();
    JSONObject updateFields = new JSONObject();
    request.put("fields", updateFields);

    for (Map.Entry<String, Object> entry : fieldValuesToUpdate.entrySet()) {

      String fieldId = fieldIds.get(entry.getKey());
      Map<String,String> allowedValues = getAllowedValues(fieldsMeta, fieldId);
      JSONObject fieldMeta = (JSONObject) fieldsMeta.get(fieldId);
      if (fieldMeta != null) {
        String fieldType = ((JSONObject) fieldMeta.get("schema")).get("type").toString();

        Object entryValue = entry.getValue();
        if (allowedValues != null) {
          updateFields.put(fieldId, handleConstrainedValueList(entryValue, allowedValues, fieldId));
        } else if ("array".equals(fieldType)) {
          updateFields.put(fieldId, handleArrayType(entryValue));
        } else {
          updateFields.put(fieldId, handleFreeEntry(entryValue));
        }
      }
    }

    return request;
  }


  protected static Object handleConstrainedValueList(Object candidateFieldValue, Map<String, String> allowedValues, String fieldId) {

    if (candidateFieldValue instanceof String) {
      String passedVal = (String) candidateFieldValue;
      return jsonObjectOrException(allowedValues, fieldId, passedVal);

    } else if (candidateFieldValue instanceof List) {
      JSONArray arr = new JSONArray();
      for (Object item : (List<String>) candidateFieldValue) {
        if (item instanceof String) {
          arr.add(jsonObjectOrException(allowedValues, fieldId, (String) item));
        } else {
          arr.add(jsonObjectOrException(allowedValues, fieldId, item.toString()));
        }
      }
      return arr;
    }

    return new JSONObject();
  }

  private static JSONObject jsonObjectOrException(Map<String, String> allowedValues, String fieldId, String passedVal) {

    if (allowedValues.containsKey(passedVal)) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("id", allowedValues.get(passedVal));
      return jsonObject;
    } else {
      throw new RuleException(passedVal + " is not an allowed value for " + fieldId);
    }
  }

  protected static Map<String,String> extractFieldIds(JSONObject fieldMeta) {
    Map<String, String> fields = new HashMap<>(fieldMeta.size());

    for (Map.Entry<String, JSONObject> entry : (Iterable<Map.Entry<String, JSONObject>>) fieldMeta.entrySet()) {
      fields.put(entry.getValue().get("name").toString(), entry.getKey());
    }

    return fields;
  }

  protected static Map<String,String> getAllowedValues(JSONObject fields, String fieldId) {

    JSONObject jsonObject = (JSONObject) fields.get(fieldId);
    if (jsonObject != null && jsonObject.containsKey("allowedValues")) {
      Map<String,String> allowedValues = new HashMap<>();

      JSONArray allowedJsonValues = (JSONArray) jsonObject.get("allowedValues");
      for (Object value : allowedJsonValues) {
        JSONObject jValue = (JSONObject) value;
        allowedValues.put((String) jValue.get("value"), (String) jValue.get("id"));
      }
      return allowedValues;
    }
    return null;
  }

  protected static JSONArray handleArrayType(Object value) {

    JSONArray arr = new JSONArray();
    if (value instanceof String) {
      arr.add(value);
    } else if (value instanceof List) {
      for (String val : (List<String>) value) {
        arr.add(val);
      }
    } else if (value instanceof Set) {
      for (String val : (Set<String>) value) {
        arr.add(val);
      }
    }
    return arr;
  }

  protected static String handleFreeEntry(Object value) {

    StringBuilder sb = new StringBuilder();

    if (value instanceof String) {

      return (String) value;
    } else if (value instanceof List) {

      for (Object val : (List<Object>) value) {
        if (val instanceof String) {
          appendValue(sb, (String) val);
        } else if (val instanceof Parameter) {
          appendValue(sb, ((Parameter) val).toString());
        }
      }
    }
    return sb.toString();
  }

  private static void appendValue(StringBuilder sb, String value) {
    if (sb.length() > 0) {
      sb.append(", ");
    }
    sb.append(value);
  }
}
