package mattecarra.accapp

import android.annotation.TargetApi
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.topjohnwu.superuser.Shell
import mattecarra.accapp.AccUtils.accStartDeamon
import mattecarra.accapp.AccUtils.accStopDeamon
import mattecarra.accapp.AccUtils.isAccdRunning
import mattecarra.accapp.AccUtils.isBatteryCharging

@TargetApi(Build.VERSION_CODES.N)
class AccTileService: TileService(){
    private val LOG_TAG = "AccTileService"

    override fun onClick() {
        super.onClick()

        val accdRunning = isAccdRunning()

        _updateTile(!accdRunning)

        //This will give the user the feeling that his actions are handled immediately. I hope no one will spam on the button.
        //otherwise this enable/disable cycle will take forever
        //I've considered to disable the tile while it's updating but it doesn't look great and google is not doing either with wifi.
        synchronized(this) {
                if (accdRunning) {
                    val tile = qsTile
                    tile.label = getString(R.string.wait) //stop deamon a bit, so I moved _updateTile before that
                    tile.updateTile()

                    accStopDeamon()

                    tile.label = getString(R.string.tile_acc_disabled)
                    tile.updateTile()
                } else
                    accStartDeamon()
        }
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        accStartDeamon()
        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
        // Do something when the user add the Tile
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    private fun updateTile() {
        if(Shell.rootAccess()) {
            _updateTile()
        } else {
            val tile = qsTile
            tile.label = getString(R.string.tile_acc_no_root)
            tile.state = Tile.STATE_UNAVAILABLE
            tile.icon = Icon.createWithResource(this, R.drawable.ic_battery_charging_full)
            tile.updateTile() // you need to call this method to apply changes
        }
    }

    private fun _updateTile(accdRunning: Boolean = isAccdRunning(), charging: Boolean = isBatteryCharging()) {
        Log.d(LOG_TAG, "_updateTile $accdRunning $charging")

        val tile = qsTile
        tile.label = getString(if(accdRunning) R.string.tile_acc_enabled else R.string.tile_acc_disabled)
        tile.state = if(accdRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon =
            Icon.createWithResource(this,
                if(accdRunning)
                    if(charging)
                        R.drawable.ic_battery_charging_80
                    else
                        R.drawable.ic_battery_80
                else
                    if(charging)
                        R.drawable.ic_battery_charging_full
                    else
                        R.drawable.ic_battery_full
            )
        tile.updateTile() // you need to call this method to apply changes
    }
}