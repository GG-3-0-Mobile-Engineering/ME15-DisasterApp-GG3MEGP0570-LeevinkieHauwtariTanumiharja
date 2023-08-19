package com.gigih.disastermap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gigih.disastermap.api.ApiResponse
import com.gigih.disastermap.api.ApiService
import com.gigih.disastermap.api.GeometriesItem
import com.gigih.disastermap.api.Objects
import com.gigih.disastermap.api.Output
import com.gigih.disastermap.api.Properties
import com.gigih.disastermap.api.ReportData
import com.gigih.disastermap.api.Result
import com.gigih.disastermap.api.Tags
import com.gigih.disastermap.presentation.MapsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MapsViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockCall: Call<ApiResponse>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)

        // Mock ApiService behavior
        Mockito.`when`(mockApiService.getListDisasterByPeriod(Mockito.anyLong())).thenReturn(mockCall)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test fetch disaster data by period`() {
        val mockResponse = ApiResponse(
            result = Result(
                objects = Objects(
                    output = Output(
                        geometries = listOf(
                            GeometriesItem(
                                coordinates = listOf(106.827153
                                    , -6.1751306687
                                ),
                                type = "Point",
                                properties = Properties(
                                    imageUrl = "https://images.petabencana.id/ad2a62be-de22-4d38-9a41-c824b0eb279b.jpg",
                                    disasterType = "haze",
                                    createdAt = "2023-08-19T14:42:12.834Z",
                                    source = "grasp",
                                    title = null,
                                    url = "ad2a62be-de22-4d38-9a41-c824b0eb279b",
                                    tags = Tags(
                                        instanceRegionCode = "ID-JK",
                                        districtId = null,
                                        localAreaId = "11017",
                                        regionCode = "3171"
                                    ),
                                    partnerIcon = null,
                                    reportData = ReportData(
                                        reportType = "haze",
                                        visibility = 1,
                                        airQuality = 1
                                    ),
                                    pkey = "322285",
                                    text = "fi",
                                    partnerCode = null,
                                    status = "confirmed"
                                )
                            )
                        )
                    )
                )
            )
        )

        // Mock Call enqueue behavior
        Mockito.doAnswer { invocation ->
            val callback = invocation.arguments[0] as Callback<ApiResponse>
            callback.onResponse(mockCall, Response.success(mockResponse))
        }.`when`(mockCall).enqueue(Mockito.any())

        viewModel = MapsViewModel(mockApiService)
        viewModel.fetchDisasterDataByPeriod(timePeriod = 64800)

        val actualResult = viewModel.apiResponse.value

        assert(actualResult == mockResponse)
    }
}