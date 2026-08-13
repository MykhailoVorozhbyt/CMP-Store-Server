package org.cmp.store.utils

import io.ktor.resources.Resource

/**
 * Classes are used by the Resources plugin to build URLs and register routes.
 */

@Resource("/")
class ServerStatus

@Resource("/auth")
class Auth {

    @Resource("authorize")
    class Authorize(val parent: Auth = Auth())

    @Resource("refresh")
    class Refresh(val parent: Auth = Auth())

    @Resource("logout")
    class Logout(val parent: Auth = Auth())
}

@Resource("/customer")
class Customers {

    @Resource("{id}")
    class Id(val parent: Customers = Customers(), val id: String)
}

@Resource("/product")
class Products {

    @Resource("discounted")
    class Discounted(val parent: Products = Products())

    @Resource("new")
    class New(val parent: Products = Products())

    @Resource("by-ids")
    class ByIds(val parent: Products = Products(), val ids: List<String> = emptyList())

    @Resource("by-category/{categoryId}")
    class ByCategory(val parent: Products = Products(), val categoryId: Long)

    @Resource("{id}")
    class Id(val parent: Products = Products(), val id: String)
}
