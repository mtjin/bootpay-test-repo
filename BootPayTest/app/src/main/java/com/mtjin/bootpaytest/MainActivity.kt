package com.mtjin.bootpaytest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.bootpay.Bootpay
import kr.co.bootpay.BootpayAnalytics
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser

class MainActivity : AppCompatActivity() {
    private val stuck = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, "5ed4d6ad4f74b4002bcab1c4")
    }

    fun onClick_request(v: View) {
        // 결제호출
        val bootUser: BootUser = BootUser().setPhone("010-1234-5678")
        val bootExtra: BootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))

        Bootpay.init(fragmentManager)
                .setApplicationId("5ed4d6ad4f74b4002bcab1c4") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KCP) // 결제할 PG 사
                .setMethod(Method.BANK) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("맥북프로's 임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(10000) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm { message ->
                    // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    if (0 < stuck) Bootpay.confirm(message) // 재고가 있을 경우.
                    else Bootpay.removePaymentWindow() // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("confirm", message)
                }
                .onDone { message ->  // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    Log.d("done", message)
                }
                .onReady { message -> Log.d("ready", message) }
                .onCancel { message -> Log.d("cancel", message) }
                .onError { message ->  // 에러가 났을때 호출되는 부분
                    Log.d("error", message)
                }
                .onClose { Log.d("close", "close") }
                .request()
    }
}
