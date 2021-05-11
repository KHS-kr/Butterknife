package kr.butterknife.talenthouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class MyPageFragment(var userId: String = "") : Fragment() {

    lateinit var rvAdapter : MainRVAdapter
    private lateinit var posts : ArrayList<PostItem>
    private lateinit var coroutineScope : CoroutineScope

    // 들어오는 spf의 id에 따라서 메뉴 보여줄지 말지
    // 마이페이지로도 작동할 수 있고, 딴사람의 이름을 눌러서 들어오면 다른사람의 화면이므로 메뉴같은게 안보인다!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}