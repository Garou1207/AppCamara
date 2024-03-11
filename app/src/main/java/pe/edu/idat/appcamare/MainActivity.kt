package pe.edu.idat.appcamare

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import pe.edu.idat.appcamare.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var file: File
    private var rutaFotoActual = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btncamara.setOnClickListener(this)
        binding.btncompartir.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btncamara -> tomarFoto()
            R.id.btncompartir -> compartirFoto()
        }
    }

    private fun compartirFoto() {
        if(rutaFotoActual!= ""){
            val fotoUri = obtenerContenidoUri(File(rutaFotoActual))
            val intentImagen = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fotoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/jpg"
            }
            val chooser = Intent.createChooser(intentImagen,"Compartir Foto")
            if(intentImagen.resolveActivity(packageManager)!=null) {
                startActivities(chooser)
            }
        }
    }

    private fun tomarFoto() {
        //abrirCamara.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            it.resolveActivity(packageManager).also {
                componente ->
                crearArchivoFoto()
                val fotoUri: Uri = FileProvider.getUriForFile(applicationContext,"pe.edu.idat.appcamare",file)
            it.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)
            }
        }
        abrirCamara.launch(intent)
    }

    private val  abrirCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK){
            /*val data = result.data!!
            val imagenBitmap = data.extras!!.get("fata") as Bitmap
            binding.ivfoto.setImageBitmap(imagenBitmap)*/
            binding.ivfoto.setImageBitmap(obtenerImagenBitmap())
        }
    }

    private fun crearArchivoFoto(){
        val directorioImg = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_",".jpg", directorioImg)
        rutaFotoActual = file.absolutePath
    }

    private fun obtenerImagenBitmap(): Bitmap{
        return BitmapFactory.decodeFile(file.toString())
    }

    private fun obtenerContenidoUri(archivoFoto: File):Uri{
        return FileProvider.getUriForFile(applicationContext,"pe.edu.idat.appcamare.fileprovider",archivoFoto)
    }

}