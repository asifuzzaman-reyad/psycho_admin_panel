package com.reyad.psycheadminpanel.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.reyad.psycheadminpanel.R
import com.reyad.psycheadminpanel.databinding.ActivityStudyBinding
import com.reyad.psycheadminpanel.main.study.ItemData
import com.reyad.psycheadminpanel.main.study.ItemGroup

private const val PDF_REQUEST_CODE = 222

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding

    private lateinit var progressBar: ProgressBar

    private var selectedFileUri: Uri? = null
    private var mUploadTask: StorageTask<*>? = null


    var batch: String? = null
    var year: String? = null
    var courseCode: String? = null
    var category: String? = null
    var chapter: String? = null
    var lessonNo: String? = null
    var lessonTitle: String? = null
    var teacher: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hooks
        progressBar = findViewById(R.id.progressBar_study)

        // ---------------------- autocomplete textView
        batch = intent.getStringExtra("batch").toString()
        when (batch) {
            "Batch 15" -> {
                year = "1st Year"
            }
            "Batch 14" -> {
                year = "2nd Year"
            }
            "Batch 13" -> {
                year = "3rd Year"
            }
            "Batch 12" -> {
                year = "4th Year"
            }
        }

        //course code
        loadCourseCode()
        loadCourseCategory()
        loadTeacher()

        //--- choose file button
        binding.btnChooseFileStudy.setOnClickListener {
            openFile()
        }

        //--- upload button
        binding.btnUploadStudy.setOnClickListener {

            courseCode = binding.acCourseCodeStudy.text.toString()
            category = binding.acCourseTopicStudy.text.toString()
            chapter = binding.acChapterNoStudy.text.toString()
            lessonNo = binding.etLessonNoStudy.text.toString()
            lessonTitle = binding.etLessonTitleStudy.text.toString()
            teacher = binding.acCourseTeacherStudy.text.toString()


            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(applicationContext, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else if (selectedFileUri == null) {
                Toast.makeText(applicationContext, "No file selected", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                uploadFileToFirebaseStorage()
            }

        }
    }

    // upload file to storage
    private fun uploadFileToFirebaseStorage() {
//        if (!validCode() || !validShort() ||!validLesson()){
//            return
//        }
        progressBar.visibility = View.VISIBLE

        val time = System.currentTimeMillis().toString()
        val fileName = courseCode + category + time + "pdf"

        val db = FirebaseStorage.getInstance().reference
        val ref = db.child("Study")
            .child(year!!)
            .child(fileName)

        mUploadTask = selectedFileUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener { uri ->

                        //
                        uploadToFirebase(uri.toString())
                        Log.d("study", "File url: $uri")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this, "Storage failed: ${it.message.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("study", "Storage failed: ${it.message.toString()}")
                    progressBar.visibility = View.GONE
                }

        }
    }

    // firebase database
    private fun uploadToFirebase(fileUrl: String) {
        val db = FirebaseDatabase.getInstance().reference
        val ref = db.child("Study")
            .child(courseCode.toString())

        val itemGroup = ItemGroup()
        ItemGroup().headerTitle = chapter.toString()

        val item = ItemData()
        item.image = fileUrl
        item.name = lessonNo.toString() + lessonTitle.toString()

        itemGroup.itemList!!.add(item)

       ref.push()
        .setValue(itemGroup)
            .addOnSuccessListener {
                Toast.makeText(this, "File save successfully", Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Database upload failed: ${it.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("study", "Database upload failed: ${it.message.toString()}")
                progressBar.visibility = View.INVISIBLE
            }
    }

    //load course code
    private fun loadCourseCode() {
        //list
        val code1 = listOf("Psy 101", "Psy 102", "Psy 103", "Psy 104", "Psy 105", "Psy 106")
        val code2 = listOf("Psy 201", "Psy 202", "Psy 203", "Psy 204", "Psy 205", "Psy 206")
        val code3 =
            listOf("Psy 301", "Psy 302", "Psy 303", "Psy 304", "Psy 305", "Psy 306", "Psy 307")
        val code4 = listOf(
            "Psy 401", "Psy 402", "Psy 403", "Psy 404", "Psy 405", "Psy 406", "Psy 407", "Psy 408"
        )

        //array adapter
        val adapter1 = ArrayAdapter(this, R.layout.material_spinner_item, code1)
        val adapter2 = ArrayAdapter(this, R.layout.material_spinner_item, code2)
        val adapter3 = ArrayAdapter(this, R.layout.material_spinner_item, code3)
        val adapter4 = ArrayAdapter(this, R.layout.material_spinner_item, code4)

        // item click listener
        when (year) {
            "1st Year" -> {
                (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter1)
            }
            "2nd Year" -> {
                (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter2)
            }
            "3rd Year" -> {
                (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter3)
            }
            "4th Year" -> {
                (binding.acCourseCodeStudy as AutoCompleteTextView?)?.setAdapter(adapter4)
            }
        }

        binding.acCourseCodeStudy.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    binding.acCourseTopicStudy.setText("")
                    binding.acChapterNoStudy.setText("")
                }
                else -> {
                    binding.acCourseTopicStudy.setText("")
                    binding.acChapterNoStudy.setText("")
                }
            }
        }

    }

    // load course Category
    private fun loadCourseCategory() {
        //list
        val courseCategory = listOf("Notes", "Books", "Questions", "Syllabus")

        // array adapter
        val adapter = ArrayAdapter(this, R.layout.material_spinner_item, courseCategory)

        //item click listener
        (binding.acCourseTopicStudy as AutoCompleteTextView?)?.setAdapter(adapter)
        (binding.acCourseTopicStudy as AutoCompleteTextView?)?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->

                when (position) {

                    0 -> {
                        binding.etCategoryNameStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.VISIBLE
                        val code = binding.acCourseCodeStudy.text.toString()
                        loadChapterNo1(code)
                        loadChapterNo2(code)
                        loadChapterNo3(code)
                        loadChapterNo4(code)
                    }

                    1 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                        binding.etCategoryNameStudy.visibility = View.VISIBLE
                    }

                    2 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                        binding.etCategoryNameStudy.visibility = View.VISIBLE
                    }

                    3 -> {
                        binding.tilCourseTeacherStudy.visibility = View.GONE
                        binding.linearNotesStudy.visibility = View.GONE
                        binding.etCategoryNameStudy.visibility = View.VISIBLE
                    }

                }

            }
    }

    //
    private fun loadTeacher() {
        val courseTeacher1 = listOf("1st", "uk", "na", "ss", "al")
        val courseTeacher2 = listOf("2nd", "ma", "sz", "jn")
        val courseTeacher3 = listOf("3rd", "ma", "sz", "jn")
        val courseTeacher4 = listOf("4th", "ma", "sz", "jn")

        val teacherAc = binding.acCourseTeacherStudy as AutoCompleteTextView?
        when (year) {
            "1st Year" -> {
                teacherAc?.setAdapter(
                    ArrayAdapter(
                        this,
                        R.layout.material_spinner_item,
                        courseTeacher1
                    )
                )
            }
            "2nd Year" -> {
                teacherAc?.setAdapter(
                    ArrayAdapter(
                        this,
                        R.layout.material_spinner_item,
                        courseTeacher2
                    )
                )
            }
            "3rd Year" -> {
                teacherAc?.setAdapter(
                    ArrayAdapter(
                        this, R.layout.material_spinner_item, courseTeacher3
                    )
                )
            }
            "4th Year" -> {
                teacherAc?.setAdapter(
                    ArrayAdapter(
                        this, R.layout.material_spinner_item, courseTeacher4
                    )
                )
            }
        }
    }

    //***************************************load chapters start************************************
    // load Chapter - 1st year
    private fun loadChapterNo1(code: String) {

        val chapter101 = listOf(
            "1.Introduction",
            "2.Sensation and perception",
            "3.Learning",
            "4.Memory and Forgetting",
            "5.Language, thinking and problem solving",
            "6.Motivation",
            "7.Emotion",
            "8.Frustration, Conflict and Stress",
            "9.Personality",
            "10.Intelligence and Creativity"
        )
        val chapter102 = listOf(
            "1.Introduction",
            "2.Socialization",
            "3.Social Perception",
            "4.Attitudes",
            "5.Group Structures and processes",
            "6.Communication",
            "7.Interpersonal Attractions",
            "8.leadership",
            "9.Mass Communication and collective Behavior"
        )
        val chapter103 = listOf(
            "1.The Scientific method and Psychology",
            "2.Designing and conducting experiment",
            "3.Scientific Problem",
            "4.Hypothesis",
            "5.Experimental variables and control of variables",
            "6.Psycho physical methods",
            "7.Experimental designs",
            "8.Quasi-Experimental design",
            "9.Writing research reports",
            "10.Ethics ofa experimental research"
        )
        val chapter104 = listOf(
            "1.Origin and Development of Sociology",
            "2.Sociological Methods",
            "3.Sociological Concepts",
            "4.Culture and Sociology",
            "5.Social Stratification, Caste and ",
            "6.Poverty",
            "7.Social Change and Mobility",
        )
        val chapter105 = listOf(
            "1.Introduction",
            "2.Data, Frequency Distribution and Graphical Representation",
            "3.Measures of Central Tendency",
            "4.Measures of Variability",
            "5.Correlation and Regression",
            "6.Transformed Scores"
        )
        val chapter106 = listOf(
            "1.Introduction of Computer",
            "2.Operating System and Utility Programs",
            "3.Introduction to Windows7",
            "4.Transferring Files from Another Computer",
            "5.Internet",
            "6.Computer Security and safety"
        )

        //array adapter
        val autoCom = binding.acChapterNoStudy as AutoCompleteTextView?
        when (code) {
            "Psy 101" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter101))
            }
            "Psy 102" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter102))
            }
            "Psy 103" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter103))
            }
            "Psy 104" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter104))
            }
            "Psy 105" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter105))
            }
            "Psy 106" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter106))
            }
        }

        val lessonNo = binding.acChapterNoStudy.text.toString()
        Log.i("study", lessonNo)

    }

    // load Chapter - 2nd year
    private fun loadChapterNo2(code: String) {

        val chapter201 = listOf(
            "1.The field of Developmental Psychology",
            "2.Research methods in Developmental Psychology",
            "3.Theories of Child Development",
            "4.Prenatal Development",
            "5.The Birth",
            "6.The Neonate",
            "7.Infancy and Childhood",
            "8.Adolescence"
        )
        val chapter202 = listOf(
            "1.Introduction",
            "2.Development and learning",
            "3.Intelligence and learning",
            "4.Motivational factors in learning",
            "5.Learning theories",
            "6.Cognitive and affective factors in learning",
            "Problem Solving",
            "Learning situations",
            "9.Evaluation of learning",
            "10.Learners who need special help"
        )
        val chapter203 = listOf(
            "1.Introduction",
            "2.Research Methods in Behavioral Neuroscience",
            "3.Neural Physiology",
            "4.Visual, Auditory and Other Senses",
            "5.Human Nervous System",
            "6.Human Evolution",
            "7.Fundamental Genetics",
            "8.Behavioral Development: The Interaction of Genetic Factor and Experience",
            "9.Cause of Brain Damage",
            "10.Neurological Disorder"
        )
        val chapter204 = listOf(
            "1.What is Anthropology?",
            "2.Cultural anthropology",
            "Language and Communication",
            "4.Ethnicity and Race",
            "5.Families, Kinship, and Descent",
            "6.Human Adaptation"
        )
        val chapter205 = listOf(
            "1.Probability",
            "2.Probability Distribution",
            "3.Sampling and Estimation",
            "4. Test of Hypothesis",
            "5.Nonparametric Tests",
            "6.Analysis variance"
        )
        val chapter206 = listOf(
            "1.Word Processing",
            "2.Spread Sheet",
            "3.Making Presentations",
            "4.Email"
        )

        //array adapter
        val autoCom = binding.acChapterNoStudy as AutoCompleteTextView?
        when (code) {
            "Psy 201" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter201))
            }
            "Psy 202" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter202))
            }
            "Psy 203" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter203))
            }
            "Psy 204" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter204))
            }
            "Psy 205" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter205))
            }
            "Psy 206" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter206))
            }
        }

        val lessonNo = binding.acChapterNoStudy.text.toString()
        Log.i("study", lessonNo)

    }

    // load Chapter - 3rd year
    private fun loadChapterNo3(code: String) {

        val chapter301 = listOf(
            "1.Functions and Origins of Psychological Testing ",
            "2.Nature and Use of psychological Test",
            "3.Test Administration , and Scoring",
            "4.Norms and the meaning of Test Scores",
            "5.Reliability",
            "6.validity",
            "7.Items analysis",
            "8.Steps in Constructing a New Test",
            "9.Social and Implications of Testing"

        )
        val chapter302 = listOf(
            "1.Introductions",
            "2.Research process",
            "3.Preparing Researching Proposal",
            "4.Literature Review, Citations, and References",
            "5.Sample and Sampling Techniques",
            "6.Measurement Concept",
            "7.Methods of Data Collection",
            "8.Ethical Principals in psychological Research"

        )
        val chapter303 = listOf(
            "1.Introduction",
            "2.Factors influences on Abnormal Behavior",
            "3.Classification and Diagnosis",
            "4.Methods of Studying abnormal Behavior",
            "5.Stress and adjustment disorders",
            "6.Anxiety-based disorders",
            "7.Somatoform and dissociative disorders",
            "8.Mood disorders",
            "9.Schizophrenia and other delusional disorders",
            "10.Personality disorders"

        )
        val chapter304 = listOf(
            "1.Introduction to I/O psychology",
            "2.Research Methods in I/O psychology",
            "3.Job Analysis",
            "4.Assessment Methods for Selection and Placement",
            "5.Selecting Employees",
            "6.performance Appraisal",
            "7.Training and development",
            "8.Job satisfaction an Organization",
            "9.Productive and Counter Productive Behavior",
            "10.Employees Health and Safety"

        )
        val chapter305 = listOf(
            "1.Introduction",
            "2.Counselling Process",
            "3.Counselling Theory and Techniques",
            "4.Career Counselling",
            "5.Uses of test in Counselling ",
            "6.Marriage, Couple , and Family Counselling"

        )
        val chapter306 = listOf(
            "1.Concept of Health Psychology",
            "2.Health Behavior and Primary Prevention",
            "3.Pain and its Management",
            "4.Health Enhancing Behaviors",
            "5.occupational Health"
        )
        val chapter307 = listOf(
            "1.Crime and Criminology",
            "2.Psychological Theory of Crime",
            "3.Different Types of Crime in Bangladesh",
            "4.Detection of Crime",
            "5.Punishment and Correctional Services"
        )

        //array adapter
        val autoCom = binding.acChapterNoStudy as AutoCompleteTextView?
        when (code) {
            "Psy 301" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter301))
            }
            "Psy 302" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter302))
            }
            "Psy 303" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter303))
            }
            "Psy 304" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter304))
            }
            "Psy 305" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter305))
            }
            "Psy 306" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter306))
            }
            "Psy 307" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter306))
            }
        }

        val lessonNo = binding.acChapterNoStudy.text.toString()
        Log.i("study", lessonNo)

    }

    // load Chapter - 4th year
    private fun loadChapterNo4(code: String) {

        val chapter401 = listOf(
            "1.The Nature of learning theories",
            "2.Pavlov's Classical conditioning",
            "3.skinner's Operant conditioning",
            "4.Avoidance Learning",
            "5.Concept Learning",
            "6.Thorndike's Connectionism",
            "7.Hull's Systematic Behavior Theory",
            "8.Tolman's Purposive Behaviorism"

        )
        val chapter402 = listOf(
            "1.Introduction",
            "2.Psychoanalytic theory of Personality: Sigmund Freud",
            "3.social psychological theory of personality: Erich Fromm",
            "4.Phenomenological theory of personality: Carl R Rogers",
            "5.Cognitive field theory: kurt Lewin",
            "6.Trait theory of personality",
            "7.Behavior theory of  personality; B.F.Skinner",
            "8.Social learning theory of personality; Albert Banduara"

        )
        val chapter403 = listOf(
            "1.Orientation to psychological Theories",
            "2.Phenomenon of Perception: Six Broad Classes",
            "3.Classical Theories",
            "4.Configurational Approach",
            "5.Adaption Level Theory",
            "6.Association Approach",
        )
        val chapter404 = listOf(
            "1.Introduction",
            "2.Early Adulthood",
            "3.Middle Adulthood/Middle-Age",
            "4.Late Adulthood/Aging"
        )
        val chapter405 = listOf(
            "1.Introduction",
            "2.Perception and Individual Decision making",
            "3.Theories of Employee Motivation",
            "4.Leadership and Power in Organization",
            "5.Conflict, Negotiation and Group Behavior",
            "6.Organizational Culture"

        )
        val chapter406 = listOf(
            "1.Introduction",
            "2.Research Methods of Clinical psychology",
            "3.Developmental disorder",
            "4.Psychological problems",
            "5.Assessment",
            "6.Clinical assessment of Behavior disorders",
            "7.General issues in psychotherapy",
            "8.Behavior therapy",
            "9.cognitive and cognitive behavior theory",
            "10.Other important psychotherapies"
        )
        val chapter407 = listOf(
            "1.Introduction",
            "2.Pattern recognition",
            "3.Attention",
            "4.Short Term Working Memory",
            "5.long Term Memory",
            "6.Memory Codes",
            "7.Mental Representation and Organizational of knowledge",
            "8.Concepts and Categories"
        )
        val chapter408 = listOf(
            "1.Positive Psychology Introduction",
            "2.The skills of walk-being can Be Learned, Taught, and Transformative",
            "3.Future Directions in Positive Psychology",
            "4.Exploring Positive Emotions",
            "5.Understanding and Cultivating a Healthy Emotional Life",
            "6.Identifying and Using Character Strengths",
            "7.Setting and Achieving Goals"
        )

        //array adapter
        val autoCom = binding.acChapterNoStudy as AutoCompleteTextView?
        when (code) {
            "Psy 401" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter401))
            }
            "Psy 402" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter402))
            }
            "Psy 403" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter403))
            }
            "Psy 404" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter404))
            }
            "Psy 405" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter405))
            }
            "Psy 406" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter406))
            }
            "Psy 407" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter407))
            }
            "Psy 408" -> {
                autoCom?.setAdapter(ArrayAdapter(this, R.layout.material_spinner_item, chapter408))
            }
        }

        val lessonNo = binding.acChapterNoStudy.text.toString()
        Log.i("study", lessonNo)

    }

    //***************************************load chapters end**************************************
    //open gallery
    private fun openFile() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
//            intent.type = "image/*"
//            intent.type = "audio/*"
//            intent.type = "video/*"
//            intent.type = "docx/*"
            intent.type = "application/pdf"
            startActivityForResult(intent, PDF_REQUEST_CODE)
        }
    }

    //view selected file
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
            }
            // show file name
            binding.tvFileNameStudy.text = "One File selected"
        } else {
            Toast.makeText(applicationContext, "Please select a file", Toast.LENGTH_SHORT).show()
        }
    }
}
