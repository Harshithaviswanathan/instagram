package com.example.instagram

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var postsAdapter: PostsAdapter? = null
    private var storiesAdapter: StoriesAdapter? = null
    private var firestore: FirebaseFirestore? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()

        val postsRecyclerView = findViewById<RecyclerView>(R.id.postsRecyclerView)
        val storiesRecyclerView = findViewById<RecyclerView>(R.id.storiesRecyclerView)

        postsAdapter = PostsAdapter(this, ArrayList())
        storiesAdapter = StoriesAdapter(ArrayList())

        postsRecyclerView.adapter = postsAdapter
        postsRecyclerView.layoutManager = LinearLayoutManager(this)

        storiesRecyclerView.adapter = storiesAdapter
        storiesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        // Add scroll listener to the Posts RecyclerView
        postsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Scroll stopped, check and play video
                    playVisibleVideoAudio()
                }
            }
        })

        fetchPostsFromFirestore()
        fetchStatusFromFirestore()

        // Set click listener for profile icon
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Fetch stories when the activity starts
        fetchStatusFromFirestore()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources
        mediaPlayer?.release()
        mediaPlayer = null
        firestore = null
    }

    override fun onStop() {
        super.onStop()
        // Clear the stories list when the activity stops
        storiesAdapter?.clearStories()
    }

    private fun fetchPostsFromFirestore() {
        // Access the "videoPosts" collection
        firestore?.collection("videoPosts")
            ?.get()
            ?.addOnSuccessListener { documents: QuerySnapshot? ->
                documents?.let {
                    val postsList: MutableList<Post> = ArrayList()
                    for (document in it) {
                        // Convert each document to a Post object
                        val post = document.toObject(Post::class.java)
                        post?.let { safePost ->
                            // Add the post to the postsList
                            postsList.add(safePost)
                        }
                    }
                    // Update the postsAdapter with the postsList data
                    postsAdapter?.updatePosts(postsList)
                }
            }
            ?.addOnFailureListener { exception: Exception? ->
                // Handle Firestore query failure here
                exception?.printStackTrace()
            }
    }

    private fun fetchStatusFromFirestore() {
        firestore?.collection("stories")
            ?.get()
            ?.addOnSuccessListener { documents: QuerySnapshot? ->
                documents?.let {
                    val statusList: MutableList<Story> = ArrayList()
                    for (document in it) {
                        val status = document.toObject(Story::class.java)
                        status?.let { safeStatus ->
                            statusList.add(safeStatus)
                        }
                    }
                    Log.d("Firestore", "Fetched ${statusList.size} stories")
                    // Update the storiesAdapter with the fetched statusList
                    storiesAdapter?.updateStatus(statusList)
                }
            }
            ?.addOnFailureListener { exception: Exception? ->
                exception?.printStackTrace()
                Log.e("Firestore", "Failed to fetch stories")
            }
    }

    private fun playVisibleVideoAudio() {
        val layoutManager = (findViewById<RecyclerView>(R.id.postsRecyclerView)).layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            val post = postsAdapter?.getPostAtPosition(position)
            val videoUrl = post?.contentUrl
            if (videoUrl != null && videoUrl != currentPlayingUrl) {
                // Stop previous playback
                stopPlayback()

                // Start new playback
                playAudioOnly(videoUrl)
                currentPlayingUrl = videoUrl
                break
            }
        }
    }

    private fun playAudioOnly(videoUrl: String) {
        try {
            mediaPlayer?.apply {
                reset() // Reset MediaPlayer before playing a new video
                setDataSource(videoUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    // Start playback after media is prepared
                    mp.start()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("MediaPlayerError", "Error occurred while playing video: $what, $extra")
                    // Handle the error here, such as showing a toast message or logging
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error playing video", Toast.LENGTH_SHORT).show()
                    }
                    true // Indicate that the error has been handled
                }
            }
        } catch (e: Exception) {
            Log.e("VideoPlaybackError", "Error playing video: ${e.message}")
            // Handle the error here, such as showing a toast message or logging
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Error playing video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                reset()
            }
        }
    }
}
