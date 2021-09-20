package com.nomanim.bax.ui.fragments.newAnnouncementActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nomanim.bax.R
import com.nomanim.bax.adapters.FeaturesSheetRecyclerView
import com.nomanim.bax.databinding.FragmentFeaturesBinding

class FeaturesFragment : Fragment(),FeaturesSheetRecyclerView.Listener {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!
    private  lateinit var bottomSheetView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFeaturesBinding.inflate(inflater,container,false)

        bottomSheetView = layoutInflater.inflate(R.layout.layout_bottom_sheet_phone_storage,container,false)


        val list = ArrayList<String>()
        list.add("8 GB")
        list.add("16 GB")
        list.add("32 GB")
        list.add("64 GB")
        list.add("128 GB")


        binding.featuresToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.featuresNextToolbarButton.setOnClickListener { findNavController().navigate(R.id.action_featuresFragment_to_descriptionFragment) }
        binding.featuresNextButton.setOnClickListener { findNavController().navigate(R.id.action_featuresFragment_to_descriptionFragment) }

        binding.chooseStorageCardView.setOnClickListener {

            setBottomSheet()
            setStorageRecyclerView(list)

        }

        return binding.root
    }

    private fun setBottomSheet() {

        if (bottomSheetView.parent != null) {

            (bottomSheetView.parent as ViewGroup).removeAllViews()
        }
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(bottomSheetView)
        bottomSheet.show()
    }

    private fun setStorageRecyclerView(storages: ArrayList<String>) {

        val recyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.phoneStorageRecyclerView)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FeaturesSheetRecyclerView(storages,this@FeaturesFragment)
        recyclerView.adapter = adapter
    }

}