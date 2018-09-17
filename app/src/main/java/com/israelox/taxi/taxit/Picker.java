package com.israelox.taxi.taxit;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.israelox.taxi.taxit.Data.LatLangs;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Picker.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Picker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Picker extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks  {

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextViewStart;
    private AutoCompleteTextView mAutocompleteTextViewStop;
    private Double latitsSelected;
    private Double longitsSelected;
    private Double latitsSelectedTwo;
    private Double longitsSelectedTwo;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private PlaceArrayAdapter mPlaceArrayAdapterStop;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));




    public double longitude;
    public LocationManager locationManager;
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private float rideDistance;
    LocationService myService;
    static boolean statusf;


    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout, mRequest, mSettings, mHistory;

    private LatLng pickupLocation;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean requestBol = false;

    private Marker pickupMarker;
    private Button callvovo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Picker() {
        // Required empty public constructor
    }

    public interface OnDataPass {
        public void onDataPass(Double latitudess, Double longitudess, Double latitudesstwo, Double longitudesstwo);
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public void passData(Double latitudess, Double longitudess, Double latitudesstwo, Double longitudesstwo) {
        dataPasser.onDataPass(latitudess, longitudess, latitudesstwo, longitudesstwo);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Picker.
     */
    // TODO: Rename and change types and number of parameters
    public static Picker newInstance(String param1, String param2) {
        Picker fragment = new Picker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        buildGoogleApiClient();



       View rootView=inflater.inflate(R.layout.fragment_picker, container, false);

        final String service = getArguments().getString("Service");






callvovo=(Button)rootView.findViewById(R.id.callvovo);
        mAutocompleteTextViewStart = (AutoCompleteTextView)rootView.findViewById(R.id
                .autoCompleteTextViewStart);
        mAutocompleteTextViewStop = (AutoCompleteTextView)rootView.findViewById(R.id
                .autoCompleteTextViewStop);

        mAutocompleteTextViewStart.setThreshold(3);
        mAutocompleteTextViewStop.setThreshold(3);



        mAutocompleteTextViewStart.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter( getContext(), android.R.layout.simple_list_item_1, null, null);
        mAutocompleteTextViewStart.setAdapter(mPlaceArrayAdapter);


        mAutocompleteTextViewStop.setOnItemClickListener(mAutocompleteClickListenerStop);
        mPlaceArrayAdapterStop = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1, null, null);
        mAutocompleteTextViewStop.setAdapter(mPlaceArrayAdapter);



callvovo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

Intent i=new Intent(getActivity(), CustomerMapActivity.class);

i.putExtra("Services", service);
i.putExtra("latitsa", latitsSelected.toString());
i.putExtra("longitsa", longitsSelected.toString());
i.putExtra("latitsatwo", longitsSelectedTwo.toString());
        i.putExtra("longistatwo", longitsSelectedTwo.toString());

//       ((CustomerMapActivity)getActivity()).book();



       startActivity(i);




    }
});


































        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        passData(latitsSelected, longitsSelected, latitsSelectedTwo, longitsSelectedTwo);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }










    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };





    private AdapterView.OnItemClickListener mAutocompleteClickListenerStop
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackStop);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };




    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            LatLangs cood=new LatLangs();


            setLatitsSelected(place.getLatLng().latitude);
            setLongitsSelected(place.getLatLng().longitude);
//            Double latit=cood.getLatitSelected();
//            Double longit=cood.getLongitSelected();
//            setLatitsSelected(latit);
//            setLongitsSelected(longit);




//            Toast.makeText(getActivity(), latitsSelected+" "+longitsSelected, Toast.LENGTH_SHORT).show();
//








//            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
//            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
//                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };





    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackStop
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            LatLangs cood=new LatLangs();


            cood.setLatitSelectedTwo(place.getLatLng().latitude);
            cood.setLongitSelectedTwo(place.getLatLng().longitude);



//            Double latittwo=cood.getLatitSelectedTwo();
//            Double longittwo=cood.getLongitSelectedTwo();
            setLatitsSelectedTwo(place.getLatLng().latitude);
            setLongitsSelectedTwo(place.getLatLng().longitude);




//            Toast.makeText(getActivity(), latitsSelectedTwo+" "+longitsSelectedTwo, Toast.LENGTH_SHORT).show();
//









//            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
//            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
//                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };





    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getContext(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }






    private void setLatitsSelected(Double latitsSelected)
    {

        this.latitsSelected=latitsSelected;
    }

    private void setLongitsSelected(Double longitsSelected)
    {

        this.longitsSelected=longitsSelected;
    }


    private void setLatitsSelectedTwo(Double latitsSelectedTwo)
    {

        this.latitsSelectedTwo=latitsSelectedTwo;
    }

    private void setLongitsSelectedTwo(Double longitsSelectedTwo)
    {

        this.longitsSelectedTwo=longitsSelectedTwo;

    }


}
