-allowaccessmodification

-overloadaggressively

-repackageclasses

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Allow enums to be deserialized when read from a Bundle
-keepclassmembers enum * {
    public static **[] values();
}
