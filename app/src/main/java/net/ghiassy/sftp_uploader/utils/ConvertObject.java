package net.ghiassy.sftp_uploader.utils;

import androidx.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import net.ghiassy.sftp_uploader.models.ServerModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class ConvertObject {

    public static String convertToJSon(@NonNull ArrayList<ServerModel> obj)
            throws NullPointerException, JsonDataException
    {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ServerModel.class);
        JsonAdapter<ArrayList<ServerModel>> adapter = moshi.adapter(type);
        String json = adapter.toJson(obj);
        return json;
    }

    public static ArrayList<ServerModel> convertFromJSon(@NonNull String json)
            throws NullPointerException, JsonDataException, IOException
    {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ArrayList<ServerModel>> adapter = moshi.adapter(Types.newParameterizedType(List.class, ServerModel.class));
        return adapter.fromJson(json);

    }
}
