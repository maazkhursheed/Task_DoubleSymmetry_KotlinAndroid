package com.rsmnm.Repositories

import com.rsmnm.Networking.WebService
import com.rsmnm.Networking.WebServiceFactory

class ApiRepository {

    private var webService: WebService

    constructor() {
        webService = WebServiceFactory.getInstance();
    }
}