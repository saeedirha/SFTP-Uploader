package net.ghiassy.sftp_uploader

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ghiassy.sftp_uploader.databinding.ActivityMainBinding
import net.ghiassy.sftp_uploader.models.ButtonViewModel
import net.ghiassy.sftp_uploader.models.FilesSharedViewModel
import net.ghiassy.sftp_uploader.models.ItemFilesModel
import net.ghiassy.sftp_uploader.models.LogShareViewModel
import net.ghiassy.sftp_uploader.models.ServerModel
import net.ghiassy.sftp_uploader.models.SharedViewModel
import net.ghiassy.sftp_uploader.models.SplashScreenViewModel
import net.ghiassy.sftp_uploader.models.UploadProgressShareModel
import net.ghiassy.sftp_uploader.utils.ConvertObject
import net.ghiassy.sftp_uploader.utils.EncryptedServerConfig
import net.ghiassy.sftp_uploader.utils.SFTPUploader
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val REQUEST_CODE_PERMISSIONS = 1


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted
                // Proceed with your application logic
                //startFileUpload()
            } else {
                // Permission is not granted
                // Close the application
                Toast.makeText(this, "Permission is not granted - Required", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private val filesSharedViewModel: FilesSharedViewModel by viewModels()
    private val uploadProgressShareModel: UploadProgressShareModel by viewModels()
    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val logSharedViewModel: LogShareViewModel by viewModels()
    private val buttonViewModel: ButtonViewModel by viewModels()
    private lateinit var filesList: ArrayList<ItemFilesModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepVisibleCondition {
                splashScreenViewModel.isLoading.value
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //Google ads
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)



        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        filesList = filesSharedViewModel.data.value!!

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MainFragment()).commit()
            binding.bottomNavigationView.menu.getItem(1).isChecked = true

            val serverListJson: String? = EncryptedServerConfig.getSharedPreferences(this)
                .getString("serverList", "")
            if (!serverListJson.isNullOrEmpty()) {
                val serverList = ConvertObject.convertFromJSon(serverListJson)
                sharedViewModel.updateList(serverList)
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.filesItem -> MainFragment()
                R.id.serversListItem -> ServerListFragment()
                R.id.logsItem -> LogsFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, it).commit()
                binding.fbtUpload.visibility = if (menuItem.itemId == R.id.filesItem) View.VISIBLE else View.INVISIBLE
                true
            } ?: false
        }

        buttonViewModel.buttonEnabled.observe(this) { enabled ->
            binding.fbtUpload.isEnabled = enabled
        }

        binding.fbtUpload.setOnClickListener {

            startFileUpload()
        }
    }

    override fun onDestroy() {
        saveSettings()
        super.onDestroy()
    }

    override fun onPause() {
        saveSettings()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                startFileUpload()
            } else {
                // Permission is not granted
                Toast.makeText(
                    this,
                    "Permission is not granted - Required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun startFileUpload() {
        val serverList = sharedViewModel.data.value
        val filesList = filesSharedViewModel.data.value

        val activeServers = serverList?.filter { it.isEnabled == true }
        if (activeServers.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Error: At least one enabled server is required.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (filesList.isNullOrEmpty()) {
            Toast.makeText(this, "Error: At least one file is required.", Toast.LENGTH_LONG)
                .show()
            return
        }

        lifecycleScope.launch {
            binding.fbtUpload.isEnabled = false
            delay(1000L)
            Toast.makeText(this@MainActivity, "Starting Upload...", Toast.LENGTH_SHORT).show()

            for (server in activeServers) {
                logSharedViewModel.appendText("Uploading to ${server.host} ...\n")
                uploadProgressShareModel.updateText("Uploading to ${server.host} ...")
                delay(3000L)

                val inputStream = filesList[0]
                    ?.let { it1 -> contentResolver.openInputStream(it1.getFileUri()) }
                if (inputStream != null) {
                    SFTPUploader(
                        applicationContext,
                        server.host,
                        server.port,
                        server.username,
                        server.password,
                        filesList[0].getFileName(),
                        filesList[0].getFileSizeLong(),
                        inputStream
                    ).uploadFile(this@MainActivity, uploadProgressShareModel)
                }

            }
            if(uploadProgressShareModel.isError.value == true) {
                uploadProgressShareModel.updateText("Upload to all failed")
            }else{
                uploadProgressShareModel.done(true)
            }

            binding.fbtUpload.isEnabled = true
        }
    }


    private fun saveSettings() {
        val currentList = sharedViewModel.data.value
        val storeData = EncryptedServerConfig.getSharedPreferences(this)
        storeData.edit().putString("serverList", ConvertObject.convertToJSon(currentList!!)).apply()
    }

    private fun checkForActiveServer(servers: List<ServerModel>): Boolean {
        return servers.any { it.isEnabled }
    }

    private fun readContent(inputStream: java.io.InputStream) {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        try {
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
                line = reader.readLine()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader.close()
        }

        val content = stringBuilder.toString()
        Log.d(TAG , content)
    }
}