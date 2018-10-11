package mobileapps.technroid.io.cabigate.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefs {

	Context context;
	SharedPreferences prefs;


	

	public SharedPrefs(Context context) {
		this.context = context;
		prefs = context.getSharedPreferences("Speego", 0);
	}
	
	
	
	
	
	
	
	
	public void clearSharedprefs() {
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}
	
	
	
	public boolean isLaucnched() {
		return prefs.getBoolean("isLaucnchingFirstTime", false);
	}

	public void setLaunched(boolean iscompletewizard) {
		Editor edit = prefs.edit();
		edit.putBoolean("isLaucnchingFirstTime", iscompletewizard);
		edit.commit();
	}

	public boolean isCompleteWizard() {
		return prefs.getBoolean("iscompletewizard", false);
	}

	public void setCompleteWizard(boolean iscompletewizard) {
		Editor edit = prefs.edit();
		edit.putBoolean("iscompletewizard", iscompletewizard);
		edit.commit();
	}


/*
	public boolean isWalkThrough() {
		return prefs.getBoolean(Const.WALK_THROUGH, true);
	}

	public void setWalkThrough(boolean walk_through) {
		Editor edit = prefs.edit();
		edit.putBoolean(Const.WALK_THROUGH, walk_through);
		edit.commit();
	}*/
	
	/*.............User INFO..............*/
	
	
	/*public void setUserINfo(UserModel userModel,Activity context){
		
		
		setAuth_token(userModel.getAccessKey());
		setUserid(userModel.getId());
		setUsername(userModel.getName());
		setEmail(userModel.getEmail());
		setAdress(userModel.getAddress());
		setPhoneNo(userModel.getPhone());
		//CommonMethods.fetchFullResolutionImage(userModel.getImagePath(), context);

		setSearchlimit(userModel.getSearchLimit());
		
	}
	
	
	
     public UserModel getUserINfo(){
		UserModel userModel=new UserModel();
		userModel.setAccessKey(getAuth_token());
        userModel.setId(getUserId());
        userModel.setName(getUsername());
	    userModel.setEmail(getEmail());
		userModel.setImagePath(getProfileImage());
		userModel.setAddress(getAdress());
		 userModel.setAddress(getPhoneNO());

		return userModel;
				
	}



	*/
	
	
/*.....profile Image........*/

/*
	public int getSearchlimit() {

		return prefs.getInt(Const.SERCH_LIMIT, 1);
	}

	public void setSearchlimit(String address) {

		int searchlimit = Integer.parseInt(address.split("-")[1]);
		Editor edit = prefs.edit();

		edit.putInt(Const.SERCH_LIMIT, searchlimit);
		edit.commit();
	}



	public void setLat(String lat){
		Editor edit = prefs.edit();
		edit.putString(Const.LAT, lat);
		edit.commit();


	};
	public String getLat(){
		return prefs.getString(Const.LAT, "0.0");
	}
	public void setLng(String lng){

		Editor edit = prefs.edit();
		edit.putString(Const.LNG, lng);
		edit.commit();


	}

	public String getLng(){

		return prefs.getString(Const.LNG, "0.0");
	}
*/

	public void setJson(String key,boolean vlaue){
		Editor edit = prefs.edit();
		edit.putBoolean("is_"+key, vlaue);
		edit.commit();
	}
	public boolean  isJson(String key){

		return prefs.getBoolean("is_"+key, false);
	}



	public void setJson(String key,String vlaue){
		Editor edit = prefs.edit();
		edit.putString(key, vlaue);
		edit.commit();
	}
	public String getJson(String key){

		return prefs.getString(key, null);
	}
	public void setJsonwaitingtime(String key,String vlaue){
		Editor edit = prefs.edit();
		edit.putString(key, vlaue);
		edit.commit();
	}
	public String getJsonwaitingtime(String key){

		return prefs.getString(key, null);
	}
	public void setJson(String key,int vlaue){
		Editor edit = prefs.edit();
		edit.putInt(key, vlaue);
		edit.commit();
	}
	public int getJsonInt(String key){

		return prefs.getInt(key, 0);
	}
     
     
     public String getDeviceToken() {
 		return prefs.getString("devicetoken", "empty");
 	}

 	public void setDevicetoken(String iscompletewizard) {
 		Editor edit = prefs.edit();
 		edit.putString("devicetoken", iscompletewizard);
 		edit.commit();
 	}
     

	public boolean isImageexist() {
		return prefs.getBoolean("isImageexist", false);
	}

	public void setImageexist(boolean iscompletewizard) {
		Editor edit = prefs.edit();
		edit.putBoolean("isImageexist", iscompletewizard);
		edit.commit();
	}
	
	public String getProfileImage() {
		return prefs.getString("profileimage", null);
	}

	public void setProfileImage(String image) {
		Editor edit = prefs.edit();
		edit.putString("profileimage", image);
		edit.commit();
	}




	public boolean isImage(
			String key) {
		return prefs.getBoolean(key, false);
	}

	public void setImage(String key,boolean isimage) {
		Editor edit = prefs.edit();
		edit.putBoolean(key, isimage);
		edit.commit();
	}

	public String getImage(String key) {
		return prefs.getString(key, null);
	}

	public void setImage(String key,String image) {
		Editor edit = prefs.edit();
		edit.putString("profileimage", image);
		edit.commit();
	}











	
   public String getGender() {
		return prefs.getString("Gender", null);
	}

	public void setGender(String gender) {
		Editor edit = prefs.edit();
		edit.putString("Gender", gender);
		edit.commit();
	}
	
	public String getDateOfBirth() {
		return prefs.getString("DateOfBirth", null);
	}

	public void setDateOfBirth(String gender) {
		Editor edit = prefs.edit();
		edit.putString("DateOfBirth", gender);
		edit.commit();
	}

	
	public String getPhoneNO() {
		return prefs.getString("PhoneNO", null);
	}

	public void setPhoneNo(String gender) {
		Editor edit = prefs.edit();
		edit.putString("PhoneNO", gender);
		edit.commit();
	}
	
	
	

	public String getCountry() {
		return prefs.getString("Country", null);
	}

	public void setCountry(String gender) {
		Editor edit = prefs.edit();
		edit.putString("Country", gender);
		edit.commit();
	}
	
	
	
	
	
	
	public String getAuth_token() {
		return prefs.getString("auth_token", null);
	}

	public void setAuth_token(String image) {
		Editor edit = prefs.edit();
		edit.putString("auth_token", image);
		edit.commit();
	}
		
	


	
	
	
	
	
	
	

	public boolean isLogin() {
		return prefs.getBoolean("islogin", false);
	}

	public void setLogin(boolean islogin) {
		Editor edit = prefs.edit();
		edit.putBoolean("islogin", islogin);
		edit.commit();
	}

	public String getUserId() {
		return prefs.getString("userid", null);
	}

	public void setUserid(String userid) {
		Editor edit = prefs.edit();
		edit.putString("userid", userid);
		edit.commit();
	}

	public String getUsername() {
		return prefs.getString("username", null);
	}

	public void setUsername(String username) {
		Editor edit = prefs.edit();
		edit.putString("username", username);
		edit.commit();
	}
	public String getAdress() {
		return prefs.getString("adress", null);
	}

	public void setAdress(String username) {
		Editor edit = prefs.edit();
		edit.putString("adress", username);
		edit.commit();
	}
	

	public String getEmail() {
		return prefs.getString("email", null);
	}

	public void setEmail(String email) {
		Editor edit = prefs.edit();
		edit.putString("email", email);
		edit.commit();
	}

	public String getPassword() {
		return prefs.getString("password", null);
	}

	public void setPassword(String password) {
		Editor edit = prefs.edit();
		edit.putString("password", password);
		edit.commit();
	}

	public String getReferralCode() {
		return prefs.getString("referralcode", null);
	}

	public void setTotalDistance(float totalDistanceInMeters) {
		Editor edit = prefs.edit();
		edit.putFloat("totalDistanceInMeters", totalDistanceInMeters);
		edit.commit();
	}

	public float getTotalDistance() {
		return prefs.getFloat("totalDistanceInMeters", 0f);
	}


	public void setPreviousLatitude(float totalDistanceInMeters) {
		Editor edit = prefs.edit();
		edit.putFloat("previousLatitude", totalDistanceInMeters);
		edit.commit();
	}

	public float getPreviousLatitude() {
		return prefs.getFloat("previousLatitude", 0f);
	}
	public void setPreviousLongitude(float totalDistanceInMeters) {
		Editor edit = prefs.edit();
		edit.putFloat("previousLongitude", totalDistanceInMeters);
		edit.commit();
	}

	public float getPreviousLongitude() {
		return prefs.getFloat("previousLongitude", 0f);
	}





	public void setfirstTimeGettingPosition(boolean firstTimeGettingPosition) {
		Editor edit = prefs.edit();
		edit.putBoolean("firstTimeGettingPosition", firstTimeGettingPosition);
		edit.commit();
	}

	public boolean getfirstTimeGettingPosition() {
		return prefs.getBoolean("firstTimeGettingPosition", true);
	}

	public void intatlizeGpsTracker(){
		setTotalDistance(0f);
		setfirstTimeGettingPosition(true);
		setPreviousLongitude(0f);
		setPreviousLongitude(0f);
		setTotalDistance(0f);

	}

	
}
