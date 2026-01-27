package com.example.watchive.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.watchive.data.remote.TmdbApiService
import com.example.watchive.data.remote.model.MoviesResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val apiService = mockk<TmdbApiService>()
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetchAllMovies is successful, it should update LiveData with movies`() = runTest {
        // Given: Mock response sukses dengan list kosong
        // Sesuaikan parameter MoviesResponse dengan constructor aslinya (page, movies, totalPages, totalResults)
        val mockResponse = MoviesResponse(1, emptyList(), 1, 0)
        
        // Gunakan any() untuk berjaga-jaga jika ada default parameter (seperti API Key) yang terpanggil
        coEvery { apiService.getPopularMovies(any(), any()) } returns Response.success(mockResponse)
        coEvery { apiService.getTopRatedMovies(any(), any()) } returns Response.success(mockResponse)
        coEvery { apiService.getNowPlayingMovies(any(), any()) } returns Response.success(mockResponse)

        // When: Inisialisasi ViewModel (akan memanggil fetchAllMovies di init)
        viewModel = HomeViewModel(apiService)
        
        // Memberikan waktu bagi coroutine untuk menyelesaikan tugasnya
        advanceUntilIdle()

        // Then: Verifikasi data
        assertNotNull(viewModel.popularMovies.value)
        assertEquals(0, viewModel.popularMovies.value?.size)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `when fetchAllMovies fails, it should show error message in LiveData`() = runTest {
        // Given: Mock API melempar Exception
        val errorMessage = "No Internet Connection"
        coEvery { apiService.getPopularMovies(any(), any()) } throws Exception(errorMessage)

        // When: Inisialisasi ViewModel
        viewModel = HomeViewModel(apiService)
        
        advanceUntilIdle()

        // Then: Verifikasi pesan error muncul
        val actualError = viewModel.errorMessage.value
        assertNotNull("Error message should not be null", actualError)
        assertTrue(actualError?.contains(errorMessage) == true)
        
        // Loading harus berhenti (false) meskipun terjadi error
        assertEquals(false, viewModel.isLoading.value)
    }
}
