package com.example.tb4;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("residuos-create") // Corresponde a tu función de Netlify para crear
    Call<Residuo> crearResiduo(@Body Residuo residuo);

    @GET("residuos-read") // Corresponde a tu función de Netlify para leer todos
    Call<List<Residuo>> obtenerResiduos();

    @GET("residuos-read/{id}") // Corresponde a tu función de Netlify para leer uno por ID
    Call<Residuo> obtenerResiduo(@Path("id") int id);

    @PUT("residuos-update/{id}") // Corresponde a tu función de Netlify para actualizar
    Call<Residuo> actualizarResiduo(@Path("id") int id, @Body Residuo residuo);

    @DELETE("residuos-delete/{id}") // Corresponde a tu función de Netlify para eliminar
    Call<Void> eliminarResiduo(@Path("id") int id);
}