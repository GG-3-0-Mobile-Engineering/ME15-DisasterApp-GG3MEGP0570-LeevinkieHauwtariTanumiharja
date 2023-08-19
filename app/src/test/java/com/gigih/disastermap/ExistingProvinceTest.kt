package com.gigih.disastermap

import com.gigih.disastermap.data.ExistingProvince
import org.junit.Assert.assertEquals
import org.junit.Test

class ExistingProvinceTest {

    @Test
    fun `get province by code - should return Jambi for code ID-JA`() {
        val province = ExistingProvince.getProvinceByCode("ID-JA")
        assertEquals(ExistingProvince.Jambi, province)
    }

    @Test
    fun `get unknown province by invalid code - should return Unknown`() {
        val province = ExistingProvince.getProvinceByCode("INVALID-CODE")
        assertEquals(ExistingProvince.Unknown, province)
    }
}