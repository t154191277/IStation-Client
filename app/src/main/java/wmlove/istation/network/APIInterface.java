package wmlove.istation.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import wmlove.istation.network.model.Cart;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.network.model.Category;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.network.model.Token;

public interface APIInterface {

    @GET("api/category")
    Call<ResponseModel<List<Category>>> getCategory(@Header("access_token") String token);

    @GET("api/goods")
    Call<ResponseModel<GoodsModel>> getGoodsByID(@Header("access_token") String token
            , @Query("id") String id);

    @GET("api/goods")
    Call<ResponseModel<List<GoodsModel>>> getGoodsByShopID(@Header("access_token") String token
            , @Query("shopId") int id,@Query("offset") int offset, @Query("limit") int limit);

    @GET("image/")
    Call<ResponseModel<GoodsModel>> getImageByID(@Header("access_token") String token
            , @Query("id") String id);

    @GET("api/cart")
    Call<ResponseModel<List<Cart>>> getCartByID(@Header("access_token") String token);

    @GET("api/goods")
    Call<ResponseModel<List<GoodsModel>>> selectGoodsByCategory(@Header("access_token") String token
            , @Query("category") String category
            , @Query("offset") int offset
            , @Query("limit") int limit
            , @Query("order") String order
            , @Query("by") String by);

    @GET("api/goods")
    Call<ResponseModel<List<GoodsModel>>> selectGoodsByName(@Header("access_token") String token
            , @Query("name") String name
            , @Query("offset") int offset
            , @Query("limit") int limit
            , @Query("order") String order
            , @Query("by") String by);

    @GET("api/goods")
    Call<ResponseModel<List<GoodsModel>>> getRecommend(@Header("access_token") String token
            , @Query("recommend") boolean recommend);

    @PUT("api/cart")
    Call<ResponseModel> updateCart(@Header("access_token") String token, @Query("goodid") String goodid
            , @Query("num") String num);

    @DELETE("api/cart")
    Call<ResponseModel> deleteCart(@Header("access_token") String token, @Query("goodid") String goodid);

    @POST("api/cart")
    Call<ResponseModel> insertCart(@Header("access_token") String token, @Query("goodid") String goodid
            , @Query("num") int num);

    @POST("account/login")
    Call<ResponseModel<Token>> login(@Query("phoneid") String id);

}
