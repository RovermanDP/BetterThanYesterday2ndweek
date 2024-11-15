
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.betterthanyesterday.ExerciseRecord
import com.example.betterthanyesterday.repository.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    private val repository = WorkoutRepository()

    private val _exerciseRecords = MutableLiveData<List<ExerciseRecord>>()
    val exerciseRecords: LiveData<List<ExerciseRecord>> get() = _exerciseRecords

    // 운동 기록 추가
    fun addExerciseRecord(record: ExerciseRecord) {
        viewModelScope.launch {
            repository.addExerciseRecord(record)
            loadExerciseRecords() // 데이터 다시 로드
        }
    }

    // 운동 기록 불러오기
    fun loadExerciseRecords() {
        viewModelScope.launch {
            _exerciseRecords.value = repository.getExerciseRecords()
        }
    }
}


