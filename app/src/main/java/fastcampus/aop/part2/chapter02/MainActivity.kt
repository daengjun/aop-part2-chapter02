package fastcampus.aop.part2.chapter02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible


/**
setonclickListenr를 implements 해서
onclick을 switch 형식으로 바꾸면 좀더 보기 쉬울거같음
앱 ui를 수정한다면 한번에 보여주는게아니라
애니메이션 효과를 줘서 숫자하나씩 fadein 효과로 보여준다면 좀더 그럴듯한 앱이 될듯
초기화 눌렀을때 다이어로그를 띄워서 사용자에게 알림 해주는것도
좋을것같고 번호 복사 하기 같은 기능도 넣어주면 무난할듯
간단한 로직과 기능의 앱이였지만 list 활용하는부분이나 , with 같은 코틀린 api를 사용함
 **/

class MainActivity : AppCompatActivity() {


    // 번호 초기화
    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    // 번호 추가
    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    // 자동 번호 생성 추가
    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }


    // 각번호 Textview 초기화
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById(R.id.textView1),
            findViewById(R.id.textView2),
            findViewById(R.id.textView3),
            findViewById(R.id.textView4),
            findViewById(R.id.textView5),
            findViewById(R.id.textView6)
        )
    }

    private var didRun = false

    private val pickNumberSet = hashSetOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNumberPicker()
        initRunButton()
        initAddButton()
        initClearButton()
    }

    // numberPicker 범위 설정
    private fun initNumberPicker() {
        numberPicker.minValue = 1
        numberPicker.maxValue = 45
    }


    // 자동 생성 시작 버튼 onclick 기능
    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true

            // 추가버튼을 하지않고 자동생성했을때 아직 추가안된 번호들을 보이도록 수정하고 색상 변경함
            // 이미 visible인 경우에는 로직 안타도록 수정해야됨
            //  if(!textView.isVisible) {} 추가함

            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]


                if (!textView.isVisible) {

                    textView.text = number.toString()
                    textView.isVisible = true

                }

                setNumberBackground(number, textView)
            }
        }
    }

    // 버튼 onclick 정의
    private fun initAddButton() {

        // 추가 버튼 onclick 기능
        addButton.setOnClickListener {

            // 번호가 가득 찼을때 didRun = True
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 사용자가 선택한 번호가 가득찼을때 (최대 5개까지로 지정)
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // arraylist에 동일한 값이 있을때 중복처리
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 위의 예외 처리에 안걸렸을때 실행할 로직

            // TextView 보이게 만들고
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            // 값을 넣고
            textView.text = numberPicker.value.toString()

            // 배경 변경
            setNumberBackground(numberPicker.value, textView)

            //arraylist에 값을 넣음
            pickNumberSet.add(numberPicker.value)
        }

    }

    // 각 숫자 별로 색상 다르게 설정
    private fun setNumberBackground(number: Int, textView: TextView) {
        when (number) {
            in 1..10 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_yello)
            in 11..20 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)

        }
    }

    // 초기화 기능
    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            // 반복하는 forEach 고차함수 사용해서 전체 텍스트뷰 visible false로 변경 (안보이도록)
            numberTextViewList.forEach {
                it.isVisible = false
            }

            didRun = false
        }

    }


    // 1~45까지 숫자중에서 랜덤 6개 List로 리턴
    private fun getRandomNumber(): List<Int> {


        // numberList 변수에 제네릭이 Int형인 List에 1~45까지 넣는데
        // pickNumberSet에 추가된 번호는 제외함
        val numberList = mutableListOf<Int>()
            // apply는 코틀린 api의 유용한 고차 함수인데 객체를 받아서 수정하고 객체를 반환함
            .apply {
                for (i in 1..45) {
                    if (pickNumberSet.contains(i)) {
                        continue
                    }

                    this.add(i)
                }
            }

        // numberList에 있는 순서를 섞음
        numberList.shuffle()

        // pickNumberSet ( 사용자가 선택한 값 ) + numberList 값 (0부터 6까지 담는데 사용자가 선택한 값이있으면 그값에서 6을빼서 담음)
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        // 정렬
        return newList.sorted()
    }
}