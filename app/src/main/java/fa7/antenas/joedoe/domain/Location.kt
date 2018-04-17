package fa7.antenas.joedoe.domain

import com.google.android.gms.maps.model.LatLng

class Location {

    private lateinit var latLng:LatLng
    private var id:Int = 0
    private lateinit var keys:Array<String>
    private lateinit var name:String
    private lateinit var url:String
    private lateinit var formatedAddress:String


    fun setName(name:String){
        this.name = name
    }

    fun setFormattedAddress(formatedAddress:String) {
        this.formatedAddress = formatedAddress
    }

    fun getFormattedAddress() : String{
        return this.formatedAddress
    }



}