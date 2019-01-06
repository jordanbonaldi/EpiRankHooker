package net.neferett.hooker.Utils;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONObjectCustom extends JSONObject {

    @Getter
    JSONObject obj;

    public JSONObjectCustom(JSONObject obj) throws JSONException {
        super(obj.toString());
        this.obj = obj;
    }

    interface Invoke<T> {
        T invoke(String name) throws JSONException;
    }

    public interface appealJson<U> {
        U addToList(JSONObjectCustom obj);
    }

    private <T> T getObj(Invoke<T> method, T def, String name)
    {
        T t = def;
        try {
            t = method.invoke(name);
        } catch (Exception ignored) {
            return t;
        }
        return t;
    }

    private boolean isArrayExists(String name) {
        try {
            this.getJSONArray(name);
        } catch(Exception ignored) {
            return false;
        }
        return true;
    }

    public boolean isExists(String name) {
        try {
            this.get(name);
        } catch(Exception ignored) {
            return false;
        }
        return true;
    }

    @Override
    public String getString(String name) {
        return this.getObj(JSONObjectCustom.super::getString, null, name);
    }

    @Override
    public boolean getBoolean(String name) {
        return this.getObj(JSONObjectCustom.super::getBoolean, false, name);
    }

    @Override
    public int getInt(String name) throws JSONException {
        return this.getObj(JSONObjectCustom.super::getInt, 0, name);
    }

    @Override
    public JSONObjectCustom getJSONObject(String name) {
        return new JSONObjectCustom(this.obj.getJSONObject(name));
    }

    public <U> List<U> arrayToList(String name, appealJson<U> json)
    {
        List<U> list = new ArrayList<>();

        if (this.isArrayExists(name)) {
            JSONArray array = this.obj.getJSONArray(name);

            for (int i = 0; i < array.length(); i++) {
                list.add(json.addToList(new JSONObjectCustom(array.getJSONObject(i))));
            }
        } else
            list.add(json.addToList(this.getJSONObject(name)));


        return list;
    }
}