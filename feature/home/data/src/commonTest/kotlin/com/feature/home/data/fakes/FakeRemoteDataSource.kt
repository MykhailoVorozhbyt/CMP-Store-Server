package com.feature.home.data.fakes

import com.feature.home.data.RemoteDataSource
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError

class FakeRemoteDataSource : RemoteDataSource {

    var discountedProductsResult: ApiResult<List<Product>, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var newProductsResult: ApiResult<List<Product>, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var productByIdResult: ApiResult<Product, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var productsByIdsResult: ApiResult<List<Product>, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var productsByCategoryResult: ApiResult<List<Product>, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)

    override suspend fun getDiscountedProducts() = discountedProductsResult
    override suspend fun getNewProducts() = newProductsResult
    override suspend fun getProduct(id: String) = productByIdResult
    override suspend fun getProductsByIds(ids: List<String>) = productsByIdsResult
    override suspend fun getProductsByCategory(category: ProductCategory) = productsByCategoryResult
}