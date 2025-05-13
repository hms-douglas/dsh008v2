package dev.dect.wear.watchface.dsh008.v2.tile;

import androidx.annotation.NonNull;

import androidx.wear.protolayout.ActionBuilders;
import androidx.wear.protolayout.DimensionBuilders;
import androidx.wear.protolayout.LayoutElementBuilders;
import androidx.wear.protolayout.ResourceBuilders;
import androidx.wear.protolayout.TimelineBuilders;
import androidx.wear.protolayout.ModifiersBuilders;

import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.EventBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Random;

import dev.dect.dsh008.v2.CalendarDrawer;
import dev.dect.wear.watchface.dsh008.v2.activity.helper.PermissionActivity;
import dev.dect.wear.watchface.dsh008.v2.utils.Utils;

public class CalendarTile extends TileService {
    /**
     * Service responsible for generating/handling the tile
     */

    private static final String ID_CALENDAR = "c",
                                ID_REFRESH = "r";

    private String RESOURCES_VERSION = "v";

    //Whenever the user navigates to the tile, or when it is added
    @Override
    protected void onTileEnterEvent(@NonNull EventBuilders.TileEnterEvent requestParams) {
        //Checks if the permissions are granted, if not request them by launching the permission activity helper
        PermissionActivity.requestCalendarPermissionIfNecessary(this);

        requestRefresh();

        super.onTileEnterEvent(requestParams);
    }

    //When the tile layout requests a resource/asset, in this case the calendar image
    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onTileResourcesRequest(@NonNull RequestBuilders.ResourcesRequest requestParams) {
        final ResourceBuilders.Resources.Builder resources = new ResourceBuilders.Resources.Builder();

        final CalendarDrawer calendarDrawer = new CalendarDrawer(this, Utils.getCurrentCalendarUserSettings(this));

        calendarDrawer.drawTile();

        resources.addIdToImageMapping(
            ID_CALENDAR,
            new ResourceBuilders.ImageResource.Builder()
            .setInlineResource(
                new ResourceBuilders.InlineImageResource.Builder()
                .setData(
                    calendarDrawer.getDrawAsByteArray()
                ).setFormat(ResourceBuilders.IMAGE_FORMAT_UNDEFINED)
                .setHeightPx(calendarDrawer.getDrawAsBitmap().getHeight())
                .setWidthPx(calendarDrawer.getDrawAsBitmap().getWidth())
                .build()
            ).build()
        );

        return Futures.immediateFuture(resources.setVersion(RESOURCES_VERSION).build());
    }

    //When the tile requests the layout
    @NonNull
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(@NonNull RequestBuilders.TileRequest requestParams) {
        if(requestParams.getCurrentState().getLastClickableId().equals(ID_REFRESH)) { //In case the user tap the tile
            requestRefresh();
        }

        return Futures.immediateFuture(
            new TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(new TimelineBuilders.Timeline.Builder()
                .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(new LayoutElementBuilders.Layout.Builder()
                        .setRoot(
                            layout()
                        ).build()
                    ).build()
                ).build()
            ).build()
        );
    }

    //Returns the image fitting all the screen
    private LayoutElementBuilders.LayoutElement layout() {
        return
        new LayoutElementBuilders.Image.Builder()
        .setResourceId(ID_CALENDAR)
        .setHeight(DimensionBuilders.expand())
        .setWidth(DimensionBuilders.expand())
        .setModifiers(
            new ModifiersBuilders.Modifiers.Builder()
            .setClickable(
                new ModifiersBuilders.Clickable.Builder()
                .setId(ID_REFRESH)
                .setOnClick(
                    new ActionBuilders.LoadAction.Builder().build()
                ).build()
            ).build()
        ).build();
    }

    private void requestRefresh() {
        /*
         * If the resources version is always the same, the tile update but it does not request the resource (onTileResourcesRequest),
         * in other words, the tile refreshes but it used a old calendar image instead of generating a new one
         */
        RESOURCES_VERSION = String.valueOf(new Random().nextFloat());

        TileService.getUpdater(this).requestUpdate(CalendarTile.class);
    }
}



