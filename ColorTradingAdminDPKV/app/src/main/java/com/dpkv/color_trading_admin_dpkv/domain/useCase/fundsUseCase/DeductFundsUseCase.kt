package com.dpkv.color_trading_admin_dpkv.domain.useCase.fundsUseCase

import com.dpkv.color_trading_admin_dpkv.domain.repo.fundRepo.FundRepository
import javax.inject.Inject

class DeductFundsUseCase @Inject constructor(
    private val fundRepository: FundRepository

) {

    suspend operator fun invoke(userID : Int,amount : Int) = fundRepository.deductFunds(userID,amount)

}