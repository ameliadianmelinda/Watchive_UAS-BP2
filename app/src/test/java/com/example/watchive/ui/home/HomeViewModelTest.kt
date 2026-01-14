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
        // Given: Skenario Berhasil (API mengembalikan data kosong)
        val mockResponse = MoviesResponse(1, emptyList(), 1, 0)
        coEvery { apiService.getPopularMovies() } returns Response.success(mockResponse)
        coEvery { apiService.getTopRatedMovies(page = 1) } returns Response.success(mockResponse)
        coEvery { apiService.getNowPlayingMovies(page = 1) } returns Response.success(mockResponse)

        // When: Panggil fungsi
        viewModel = HomeViewModel(apiService)
        advanceUntilIdle()

        // Then: Pastikan data masuk (size 0 karena mock kita emptyList)
        assertEquals(0, viewModel.popularMovies.value?.size)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `when fetchAllMovies fails, it should show error message in LiveData`() = runTest {
        // Given: Skenario Gagal (API dipaksa lempar Error/Exception)
        val errorMessage = "No Internet Connection"
        coEvery { apiService.getPopularMovies() } throws Exception(errorMessage)

        // When: Panggil fungsi
        viewModel = HomeViewModel(apiService)
        advanceUntilIdle()

        // Then: Pastikan pesan error muncul di LiveData errorMessage
        val actualError = viewModel.errorMessage.value
        assertTrue(actualError?.contains(errorMessage) == true)
        
        // Pastikan loading tetap berhenti (false) meskipun error
        assertEquals(false, viewModel.isLoading.value)
    }
}
