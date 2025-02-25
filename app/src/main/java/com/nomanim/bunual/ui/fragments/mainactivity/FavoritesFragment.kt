package com.nomanim.bunual.ui.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nomanim.bunual.R
import com.nomanim.bunual.adapters.AllPhonesAdapter
import com.nomanim.bunual.base.BaseFragment
import com.nomanim.bunual.models.ModelAnnouncement
import com.nomanim.bunual.base.responseToList
import com.nomanim.bunual.databinding.FragmentFavoritesBinding
import com.nomanim.bunual.viewmodel.FavoritesViewModel
import gun0912.tedimagepicker.util.ToastUtil

class FavoritesFragment : BaseFragment(), AllPhonesAdapter.Listener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var allFavoritePhones = ArrayList<ModelAnnouncement>()
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser


        mFavoritesViewModel.getIds(firestore, auth.currentUser?.phoneNumber.toString())
        initFavoritesViewModel()
    }

    private fun initFavoritesViewModel() {
        mFavoritesViewModel.idsLiveData().observe(viewLifecycleOwner, { response ->
            if (response.size() != 0) {
                val list = ArrayList<String>()
                for (doc in response.documents) {
                    val originalAdsId = doc.get("originalAdsId") as String
                    list.add(originalAdsId)
                    mFavoritesViewModel.getFavorites(firestore, list)
                }
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        })
        mFavoritesViewModel.favoritesLiveData().observe(viewLifecycleOwner, { response ->
            allFavoritePhones.responseToList(
                firestore,
                resources.getString(R.string.all_announcements),
                response
            )
            binding.progressBar.visibility = View.INVISIBLE
            setFavoritesPhonesRecyclerView()
        })
        mFavoritesViewModel.errorLiveData().observe(viewLifecycleOwner, { message ->
            binding.progressBar.visibility = View.INVISIBLE
            ToastUtil.showToast("error: $message")
        })
    }

    private fun setFavoritesPhonesRecyclerView() {
        val adapter = AllPhonesAdapter(
            requireContext(),
            allFavoritePhones,
            this@FavoritesFragment
        ) { model ->
            mMainActivity.intentToAdsDetails(model)
        }
        val fp = binding.favoritesPhones
        fp.isNestedScrollingEnabled = false
        fp.setHasFixedSize(true)
        fp.layoutManager = LinearLayoutManager(requireContext())
        fp.adapter = adapter
    }

}