package com.example.dandashkora

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dandashkora.Adapter.MatchAdapter
import com.example.dandashkora.VideoPlayer.selectedmatch
import com.example.dandashkora.model.MatchItem
import com.example.dandashkora.model.MatchResponse
import com.example.dandashkora.network.RetrofitClient
import com.example.dandashkora.databinding.FragmentMatchesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ARG_MATCH_DAY = "match_day"

class MatchListFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!
    private lateinit var matchAdapter: MatchAdapter
    private var matchDay: String? = null
    private var allMatches: List<MatchItem> = emptyList()
    private var channelsMap: Map<String, Map<String, Map<String, String>>> = emptyMap()

    companion object {
        @JvmStatic
        fun newInstance(matchDay: String) =
            MatchListFragment().apply {
                arguments = Bundle().apply { putString(ARG_MATCH_DAY, matchDay) }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { matchDay = it.getString(ARG_MATCH_DAY) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAllMatches()
    }

    private fun setupRecyclerView(matches: List<MatchItem>) {
        if (::matchAdapter.isInitialized) {
            matchAdapter.updateMatches(matches)
        } else {
            matchAdapter = MatchAdapter(matches) { match ->
                val matchServers = channelsMap[match.channel] ?: emptyMap()
                if (matchServers.isNotEmpty()) {
                    val intent = Intent(requireContext(), selectedmatch::class.java)
                    intent.putExtra("MATCH_SERVERS", HashMap(matchServers))
                    startActivity(intent)
                }
            }

            binding.recyclerViewMatches.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = matchAdapter
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAllMatches() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getMatches()
                if (response.isSuccessful) {
                    val body: MatchResponse? = response.body()
                    val matchList = body?.matches ?: emptyList()

                    // Save channels map safely
                    channelsMap = body?.channels as? Map<String, Map<String, Map<String, String>>> ?: emptyMap()

                    // Keep all matches
                    allMatches = matchList

                    withContext(Dispatchers.Main) { filterMatches() }
                } else {
                    Log.e("API_ERROR", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterMatches() {
        if (matchDay == "All Matches") {
            setupRecyclerView(allMatches)
            return
        }

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        val targetDate = when (matchDay) {
            "Yesterday" -> yesterday
            "Today" -> today
            "Tomorrow" -> tomorrow
            else -> today
        }

        val targetDateString = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val filteredList = allMatches.filter { it.date == targetDateString }
        setupRecyclerView(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
