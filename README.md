# DS H008 v2 - Calendar watch face, complication and widget
Current versions (click the badge to download the apk): 
[![version](https://img.shields.io/badge/watch-v1.0.0-FBD75B)](https://hms-douglas.github.io/dsh008v2/dist/apks/watch/1.0.0.apk)
[![version](https://img.shields.io/badge/phone-v1.0.0-FBD75B)](https://hms-douglas.github.io/dsh008v2/dist/apks/phone/1.0.0.apk)

Change README language: 
[![en](https://img.shields.io/badge/lang-en-blue.svg)](https://github.com/hms-douglas/dsh008v2)
[![pt-br](https://img.shields.io/badge/lang-pt--br-blue.svg)](https://github.com/hms-douglas/dsh008v2/blob/main/readme/pt_br/README.md)
</br>
</br>
DS H008 v2 is the second version of the calendar watch face. It is built using WFF (Watch Face Format v2) and a static complication (the calendar), therefore it can now be installed on watches with Wear OS 5 and bellow. Besides the watch face, now the watch app contains a tile that follows the same style (in case you don't want to use the watch face).
The project also includes a phone app. It can be used to install/update the watch app easily. The phone app provides a widget to be used on the phone home screen as well.
</br>
</br>
TODO ADICIONAR IMAGEM AQUI
##
### Features
<ul>
  <li>Watch app:
    <ul>
      <li>Calendar watch face;</li>
      <li>Calendar complication.</li>
    </ul>
  </li>
  <li>Phone app:
    <ul>
      <li>Calendar widget;</li>
      <li>Watch face installer.</li>
    </ul>
  </li>
</ul>

##
### Installing the watch face
#### Option 1:
- Download manually the watch apk¹(link above) and install it using adb or a specific app (you can use "bugjaeger" / search it on google play / there are tons of videos on youtube teaching how to sideload apks/apps to your watch).
  
#### Option 2:
- Download manually the phone apk¹ and install it on your phone²;
- Once it is intalled, open the phone app;
- Click on the more icon (three dots, top right side);
- Click on "Install on watch";
- Follow the instructions to install the app on your watch.

#### Option 3:
- Download the source code³, or clone this repository³, and build the app using Android Studio.
</br>
¹ Links to download the latest version are at the top of this document (badges). All apks inside this repository were built by me and are not minified;
</br>
² Google Play Protect may prevent the app from being installed using the apk file (as the app is not on google play). In this case, disable the Google Play Protect, install the file and then enable it again;
</br>
³ Check license topic bellow.

##
### Donations
- If you would like to support me, you can make a donation clicking on the button bellow... Thank you! ❤️
  <a href="https://www.paypal.com/donate/?hosted_button_id=YY4PVZXZZQN6L">
  <img src="readme/en/paypal.png" width="160" height="50"/>
  </a>

##
### FAQ (Saving your time [and mine])

1) Where can I download the version 1?
> Version 1 is available on google play for free, you can officially download it [here](https://play.google.com/store/apps/details?id=dev.dect.wear.watchface.dsh008). Version 1 uses the old jetpack api to build the watch face, therefore it is not available for watches that comes with Wear OS 5. It also has some limitations (it shows only the current half of the day, for example) and a few bugs (reported).

2) Will you update the version 1 calendar code to version 2 (this code) on google play?
> No, 2 reasons:
 > One, I lost (backups are outdated) the code/projects for a couple of projects I have on google play (no way of updating them, unless I build them from zero or work on the outdated backups);
 > Two, this code (as I am using WFF and I built from zero haha) is completely different from the one on version 1, so a lot of adaptations would be necessary (again version 1 uses the jetpack api).

3) Why don't you upload this version on google play?
> Well, 3 reasons (sort of):
 > One, I'm not in the mood to deal with google's "rules" and reviews for wear os publishing, and there are quite a few;
 > Two, even if I was in the mood, it wouldn't be approved. One of google's new rules for watch faces is that they must be build using WFF, and the .aab file [MUST NOT](https://developer.android.com/training/wearables/wff/setup#declare-wff-use) contain any code. As my watch face file contains a complication (code) they wouldn't aprove it. Only if I publish the watch face and the complication in separated project, then you would install both and set them by yourself (😖, for sure it would give me some headache, with some users).
 > Three, this project is build using complication. Complications are complicated (haha), they have limitations, especially when it comes to update frequency. In other words, this project have some "delays" when it comes to updating the calendar (for example, it sets coding alarms and sometimes you might need to tap the watch face to update), combine this "issue" with google's review policy and it wouldn't be approved...

4) Does it shows tomorrow events? What is the scissor icon?
> Only if it starts today. Even though it would be "cut" at midnight. The watch face shows only the events from today!
> The scissor icon is for events that had their "time cut" to be displayed (the event time won't change on your calendar, it will only be displayed differently, for example, if it ends after midnight it will be displayed (today) until midnight and after midnight it will display as if it started midnight).

5) Why can't I add more than one watch face?
> As I need an external way of updating the complication, adding multiple "favorites" would get complex...

6) When does it updates/refresh? What frequency?
> Seven cases:
 > 1, you tap the watch face;
 > 2, you open and close the watch app;
 > 3, approximately one minute after a event ends;
 > 4, every 1 hour after the last update (in case of no events, if there is an event it falls back on case 3);
 > 5, when you add/remove/update and event (phone app must be installed for this to work);
 > 6, when day changes;
 > 7, when you change/set the watch face.

7) Why it is not updating/refreshing?
> Well, Wear OS limits what an app can do (so it saves battery, as it is badly optimized). Check if the app was not added to the sleeping list (sometimes Wear OS adds it automatically to this list), in case it is, it won't update automatically until you remove it, you will need to tap the watch face to update. Also, it might not update while the watch is in some modes, like: aod, sleeping, battery saving, etc. Is also worth saying that syncing between your phone and watch takes some time (a few seconds usually), therefore events might not be added to the watch face "instantaneously".

8) Can I change the order of the events? Can I add a custom event to the app? Can I change the event color? Where does the events come from?
> The events comes from the WearableCalendarContract (watch app) and CalendarContract (phone app). As long as your calendar app uses the CalendarContract api the data should be rendenred by this app
app without any problem. It will display all day events first then order the rest by their start time. For now is not possible to add custom events (maybe in a future update, maybe!). The event info is the one your calendar provides (title, color, start and end time) you must set those infos inside your calendar app.

9) Why is the event not showing?
> I don't know. If the event has ended it won't be rendered. If it is a "task" it won't be rendered, only events are rendered. If it has an "unusual" character in its title it won't render. Maybe the events are overlapping and there is no space left (max of 3 rings). Maybe is the ending/starting time. Maybe the watch has removed the calendar permission and you need to grant it again (open the app or go to the watch settings). Or a bug (let me know, with specific details!)...

10) Can I set a digital clock?
> Ish. I added an analog hand with a digital clock on it. The WFF is limited, I cannot change the position of the complications after it is set, so adding a digital clock like the one on version 1 is not possible.

11) Why the complication color doesn't follow the ones I set?
> WFF fault. It happens with other watch faces too.

12) Can I change the tile hand style/color?
> No, for now you can only show/hide the minute hand, again, maybe in the future.

13) Why a tile?
> Updates only when you navigates to it, so it saves more battery, besides some people might not like the watch face but still want a different kind of calendar on their watch.

14) Why no digital hand on widget?
> Too complicated to add, widgets design are also limited, for a good reason. Maybe in future I add a digital clock widget with the calendar around it.

15) Does the watch and phone settings sync?
> Yes, but you have to click on sync every time you wish to make the watch match the phones settings.

##
### Log (watch)
<b>v1.0.0</b>
<ul>
  <li>Release.</li>
</ul>

##
### Log (phone)
<b>v1.0.0</b>
<ul>
  <li>Release.</li>
</ul>

##
### License
This project has no license ("without a license")!

Check github's rules for [choosing the right license](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/licensing-a-repository#choosing-the-right-license) to understand what it means...