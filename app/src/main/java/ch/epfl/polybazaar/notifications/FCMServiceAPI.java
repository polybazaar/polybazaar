package ch.epfl.polybazaar.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMServiceAPI {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAmiGRUFw:APA91bF3_zNZsIBMEJ4yZ6DX8W0euZlFBYIKQIQUpoonKSnyaD_e20hgsq-6rzT64gXJZflAoJwsW48LDV-g1dNN4Qo4m67aD7JYY_h4eytBlDY0IN9Bslrl93etJSCMe8nYsu3ig5_y"
            }
    )

    @POST("fcm/send")
    Call<Void> sendNotification(@Body NotificationSender body);
}
