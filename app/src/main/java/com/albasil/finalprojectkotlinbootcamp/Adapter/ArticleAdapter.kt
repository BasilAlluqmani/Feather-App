package com.albasil.finalprojectkotlinbootcamp.Adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.albasil.finalprojectkotlinbootcamp.R
import com.albasil.finalprojectkotlinbootcamp.data.Article
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.albasil.finalprojectkotlinbootcamp.UI.HomePageDirections
import com.google.firebase.auth.FirebaseAuth


class ArticleAdapter(private val articleList:ArrayList<Article>):RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {
    val currentUserUid =FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val article= articleList[position]
        holder.titleArticle.text = article.title
        holder.articleDate = article.date
        holder.userName.text = article.userName
        holder.articleCategory = article.category
        holder.articleDescription = article.description
        holder.image=article.articleImage
        holder.userId = article.userId
        holder.numberLikes.text =article.like.toString()





        holder.userName.setOnClickListener {



            if (currentUserUid.toString()==holder.userId.toString()){
                findNavController(holder.itemView.findFragment()).navigate(R.id.profile)

            }else{
                val userInformation = HomePageDirections.actionHomePageToUserProfile(holder.userId.toString())
                findNavController(holder.itemView.findFragment()).navigate(userInformation)
            }
        }

        //-------------------------------------------------------------------------
        val storageRef= FirebaseStorage.getInstance().reference
            .child("/imagesArticle/${article.articleImage.toString()}")

        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.imageArticle.load(bitmap)
        }.addOnFailureListener{}
        //------------------------------------------------------------------------------------

    }

    override fun getItemCount(): Int {

        return articleList.size

    }




    class MyViewHolder(itemView :View):RecyclerView.ViewHolder(itemView),View.OnClickListener{


       val titleArticle :TextView =itemView.findViewById(R.id.tvTitle_xml)
       val userName :TextView =itemView.findViewById(R.id.tvUserName_xml)
       val category :TextView =itemView.findViewById(R.id.tvCategoryItem_xml)
       val imageArticle :ImageView =itemView.findViewById(R.id.imageItem_xml)
        val numberLikes :TextView=itemView.findViewById(R.id.numberLike_xml)

         lateinit var articleCategory :String
         lateinit var articleDate :String
         lateinit var articleDescription :String
         lateinit var image :String
         lateinit var userId :String

         //description
       init {
           itemView.setOnClickListener(this)
       }


      override fun onClick(v: View?) {

          val article_data =Article()

          article_data.title = titleArticle.text.toString()
          article_data.userName = userName.text.toString()
          article_data.date = articleDate.toString()
          article_data.category = articleCategory.toString()
          article_data.description = articleDescription.toString()
          article_data.articleImage = image.toString()
          article_data.like = numberLikes.text.toString().toInt()


          val itemData = HomePageDirections.actionHomePageToArticleInformation(article_data)
          findNavController(itemView.findFragment()).navigate(itemData)



       }


   }



    /*fun shareArticle2(titleArticle:String,subjectArticle:String,itemView: View){

        val shareArticle = Intent(Intent.ACTION_SEND)
        shareArticle.type = "text/plain"
        shareArticle.putExtra(Intent.EXTRA_TEXT,subjectArticle.toString())
        shareArticle.putExtra(Intent.EXTRA_TITLE,titleArticle.toString())
        shareArticle.putExtra(Intent.EXTRA_SUBJECT,subjectArticle)

        startActivity(shareArticle)
    }*/





}