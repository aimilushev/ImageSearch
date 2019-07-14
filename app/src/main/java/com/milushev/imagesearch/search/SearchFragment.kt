package com.milushev.imagesearch.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.milushev.imagesearch.R
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.extensions.hideKeyboardFrom
import com.milushev.imagesearch.utils.EventObserver
import com.milushev.imagesearch.utils.ServiceLocator
import kotlinx.android.synthetic.main.fragment_search.*


private const val COLUMNS_IN_GRID = 3

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()

        initRecyclerView()

        viewModel.foundPhotos.observe(viewLifecycleOwner, Observer<PagedList<Photo>> {
            adapter.submitList(it)
        })

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer<Boolean> { visible ->
            progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })

        //TODO: enable voice search on searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchQuery.value = query
                requireContext().hideKeyboardFrom(searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun initRecyclerView() {
        photosRecyclerView.layoutManager = GridLayoutManager(requireContext(), COLUMNS_IN_GRID)

        adapter = PhotosAdapter(lifecycleScope)
        photosRecyclerView.adapter = adapter

    }

    private fun getViewModel() = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(requireContext())
                .getRepository()
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repo) as T
        }
    })[SearchViewModel::class.java]
}

