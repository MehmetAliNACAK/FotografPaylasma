package com.example.fotografpaylasma.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.fotografpaylasma.databinding.FragmentYuklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class YuklemeFragment : Fragment() {

    private var _binding: FragmentYuklemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activtyResultLauncher  : ActivityResultLauncher<Intent>
    private lateinit var  permissionLauncher : ActivityResultLauncher<String>
    private lateinit var aut : FirebaseAuth
    var secilenGorsel  : Uri? = null
    var secilenBitMap  : Bitmap? = null
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aut = Firebase.auth
        db = Firebase.firestore

        registerLaunchers()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =FragmentYuklemeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yukleBotton.setOnClickListener { YukleTiklandi(it) }
        binding.imageView.setOnClickListener { gorselSec(it) }
    }
    fun YukleTiklandi(view: View){

        //Yükleme
        if(aut.currentUser != null){

            val postMap = hashMapOf<String,Any>()
            postMap.put("email",aut.currentUser!!.email.toString())
            postMap.put("comment",binding.commentText.text.toString())
            postMap.put("date",Timestamp.now())

            db.collection("Posts").add(postMap).addOnSuccessListener {
                DocumentsReferances ->
                //veri database yüklendi
                val Action = YuklemeFragmentDirections.actionYuklemeFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(Action)
            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }

    }
    fun gorselSec(view: View){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //read media images

            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                    //İZİN YOK
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                            //izin mantığını kullanıcıya göstermek lazım
                            Snackbar.make(view,"Galeriye Gitmek için izin vermeniz gerekiyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver"
                                ,View.OnClickListener {
                                    //izin istememiz lazım
                                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)


                                }).show()
                    }else{
                        //izin istememiz lazım
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }

            }else{
                //İZİN VAR
                //GALERİYE GİT

                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activtyResultLauncher.launch(intentToGallery)
            }


        }else{
            //read external strage
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //İZİN YOK
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //izin mantığını kullanıcıya göstermek lazım
                    Snackbar.make(view,"Galeriye Gitmek için izin vermeniz gerekiyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver"
                        ,View.OnClickListener {
                            //izin istememiz lazım
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                        }).show()
                }else{
                    //izin istememiz lazım
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }

            }else{
                //İZİN VAR
                //GALERİYE GİT

                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activtyResultLauncher.launch(intentToGallery)
            }


        }




    }

    private fun registerLaunchers(){
        activtyResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode== RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!= null){
                   secilenGorsel =  intentFromResult.data
                    try {
                            if(Build.VERSION.SDK_INT>=28){
                                val source = ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                                secilenBitMap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(secilenBitMap)
                            }else{
                                    secilenBitMap =MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                                binding.imageView.setImageBitmap(secilenBitMap)
                            }
                    }catch (e : Exception){
                            e.printStackTrace()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result->
            if(result){

                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activtyResultLauncher.launch(intentToGallery)



            }else{
                    Toast.makeText(requireContext(),"İzni Reddettiniz ,izne ihtiyacımız var", Toast.LENGTH_LONG).show()



            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}