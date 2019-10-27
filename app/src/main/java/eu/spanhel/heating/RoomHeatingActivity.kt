package eu.spanhel.heating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_room_heating.*

class RoomHeatingActivity : AppCompatActivity() {

    private var listOfRooms = arrayOf("Ložnice", "Pokojík", "Kotelna", "Obývák", "Kuchyň")
    var spinner:Spinner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_heating)

        spinner = this.room_spinner

        var aa = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfRooms)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = aa

    }

}
