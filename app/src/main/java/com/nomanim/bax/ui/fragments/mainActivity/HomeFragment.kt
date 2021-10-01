package com.nomanim.bax.ui.fragments.mainActivity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bax.R
import com.nomanim.bax.adapters.HorizontalOrderAdapter
import com.nomanim.bax.adapters.VerticalOrderAdapter
import com.nomanim.bax.databinding.FragmentHomeBinding
import com.nomanim.bax.models.ModelAnnouncement
import com.nomanim.bax.ui.other.getDataFromFireStore
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(),HorizontalOrderAdapter.Listener,VerticalOrderAdapter.Listener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val sortTexts = ArrayList<String>()
    private var mostViewedPhones = ArrayList<ModelAnnouncement>()
    private var allPhones = ArrayList<ModelAnnouncement>()
    private lateinit var verticalRecyclerViewAdapter: VerticalOrderAdapter

    private var currentUserPhoneNumber: String = ""
    private val numberOfAnnouncement = 10L  //for load data limit from fireStore for once
    private lateinit var lastValue: QuerySnapshot
    private var announcementsAreOver = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserPhoneNumber = auth.currentUser?.phoneNumber.toString()

        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation?.visibility = View.VISIBLE

        getMostViewedPhonesFromFireStore()
        getAllPhonesFromFireStore()

        return binding.root
    }

    private fun addSortTextsToList() {

        for (text in resources.getStringArray(R.array.sort_data_texts) ) {
            sortTexts.add(text)
        }
    }

    private fun getMostViewedPhonesFromFireStore() {

        firestore.collection("All Announcements")
            .orderBy("numberOfViews", Query.Direction.DESCENDING).limit(numberOfAnnouncement).get().addOnSuccessListener { values ->

                binding.mostViewedProgressBar.visibility = View.INVISIBLE
                mostViewedPhones.getDataFromFireStore(firestore,"All Announcements",values)
                setHorizontalRecyclerView()

        }.addOnFailureListener { exception ->

                binding.mostViewedProgressBar.visibility = View.INVISIBLE
                context?.let { Toast.makeText(it,exception.localizedMessage?.toString(),Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getAllPhonesFromFireStore() {

        firestore.collection("All Announcements")
            .limit(numberOfAnnouncement).orderBy("time",Query.Direction.ASCENDING).get().addOnSuccessListener { values ->

            binding.allPhonesProgressBar.visibility = View.INVISIBLE
            allPhones.getDataFromFireStore(firestore,"All Announcements",values)
            setVerticalRecyclerView()

            if (values.size() != 0) {

                lastValue = values
                getMorePhonesFromFireStore()
            }

        }.addOnFailureListener { exception ->
            binding.allPhonesProgressBar.visibility = View.INVISIBLE
            context?.let { Toast.makeText(it,exception.localizedMessage,Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getMorePhonesFromFireStore() {

        val scrollView = binding.nestedScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {

            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {

                if (!announcementsAreOver) {

                    binding.morePhonesProgressBar.visibility = View.VISIBLE

                    firestore.collection("All Announcements")
                        .orderBy("time",Query.Direction.ASCENDING)
                        .startAfter(lastValue.documents[lastValue.size()-1])
                        .limit(numberOfAnnouncement).get().addOnSuccessListener { values ->

                            if (values.size() < numberOfAnnouncement) {
                                announcementsAreOver = true
                            }
                            binding.morePhonesProgressBar.visibility = View.INVISIBLE

                            lastValue = values

                            val morePhones = ArrayList<ModelAnnouncement>()
                                .getDataFromFireStore(firestore,"All Announcements",values)

                            for (i in 0 until morePhones.size) {

                                allPhones.add(morePhones[i])

                            }
                            verticalRecyclerViewAdapter.notifyDataSetChanged()
                        }
                }
            }
        }
    }

    private fun setHorizontalRecyclerView() {

        val hrv = binding.horizontalRecyclerView
        hrv.setHasFixedSize(true)
        hrv.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL)
        val horizontalRecyclerViewAdapter = HorizontalOrderAdapter(mostViewedPhones,this@HomeFragment)
        hrv.adapter = horizontalRecyclerViewAdapter
    }

    private fun setVerticalRecyclerView() {

        val vrv = binding.verticalRecyclerView
        vrv.isNestedScrollingEnabled = false
        vrv.setHasFixedSize(true)
        vrv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        context?.let {

            verticalRecyclerViewAdapter = VerticalOrderAdapter(it,allPhones,this@HomeFragment)
            vrv.adapter = verticalRecyclerViewAdapter
        }
    }

    override fun setOnClickHorizontalAnnouncement() {

        findNavController().navigate(R.id.action_homeFragment_to_showDetailsFragment)
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.visibility = View.GONE
    }

    override fun setOnClickVerticalAnnouncement() {

        findNavController().navigate(R.id.action_homeFragment_to_showDetailsFragment)
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.visibility = View.GONE
    }

}