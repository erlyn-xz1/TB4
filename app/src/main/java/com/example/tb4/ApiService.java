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
    @POST("residuos-create") //    Crear un nuevo residuo
    Call<Residuo> crearResiduo(@Body Residuo residuo);

    @GET("residuos-read") //    Leer los residuos
    Call<List<Residuo>> obtenerResiduos();

    @GET("residuos-read/{id}") //  Leer un residuo por ID ingresado
    Call<Residuo> obtenerResiduo(@Path("id") int id);

    @PUT("residuos-update/{id}") //   Actualizar un residuo por ID
    Call<Residuo> actualizarResiduo(@Path("id") int id, @Body Residuo residuo);

    @DELETE("residuos-delete/{id}") //  Eliminar un residuo por ID
    Call<Void> eliminarResiduo(@Path("id") int id);
}