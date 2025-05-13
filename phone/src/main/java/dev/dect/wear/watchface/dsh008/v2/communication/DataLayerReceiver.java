package dev.dect.wear.watchface.dsh008.v2.communication;


import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayerReceiver extends WearableListenerService {
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        if(DataLayerSender.IS_THE_ONE_CHANGING) {
            DataLayerSender.IS_THE_ONE_CHANGING = false;
        }
    }
}
