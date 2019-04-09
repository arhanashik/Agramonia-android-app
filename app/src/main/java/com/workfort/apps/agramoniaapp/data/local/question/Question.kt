package com.workfort.apps.agramoniaapp.data.local.question

import android.content.Context
import com.workfort.apps.AgramoniaApp
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsGlobal
import org.json.JSONArray
import org.json.JSONObject

data class Question(val sl: Int,
                    val question: String,
                    var answer: String,
                    val hasImage: Boolean,
                    val images: ArrayList<String>) {
    companion object {
        fun prepareQuestions(context: Context): ArrayList<Question> {
            val questions = ArrayList<Question>()

            var question = Question(
                    1,
                    context.getString(R.string.hint_answer1),
                    "",
                    true,
                    ArrayList())
            questions.add(question)

            question = Question(
                    2,
                    context.getString(R.string.hint_answer2),
                    "",
                    true,
                    ArrayList())
            questions.add(question)

            question = Question(
                    3,
                    context.getString(R.string.hint_answer3),
                    "",
                    true,
                    ArrayList())
            questions.add(question)

            question = Question(
                    4,
                    context.getString(R.string.hint_answer4),
                    "",
                    false,
                    ArrayList())
            questions.add(question)

            question = Question(
                    5,
                    context.getString(R.string.hint_answer5),
                    "",
                    false,
                    ArrayList())
            questions.add(question)

            return questions
        }

        fun prepareAnswersJsonStr(questions: ArrayList<Question>, langCode: String): String {
            val ans = arrayOf("", "", "", "", "")

            if(langCode == PrefsGlobal.selectedLanguageCode) {
                var i = 0
                questions.forEach {
                    ans[i] = it.answer
                    i++
                }
            }

            val ctx = AgramoniaApp.getBaseApplicationContext()
            val answerJson = JSONObject()
            when(langCode) {
                Const.LanguageCode.ROMANIAN -> {
                    answerJson.put(ctx.getString(R.string.question_1_ro), ans[0])
                    answerJson.put(ctx.getString(R.string.question_2_ro), ans[1])
                    answerJson.put(ctx.getString(R.string.question_3_ro), ans[2])
                    answerJson.put(ctx.getString(R.string.question_4_ro), ans[3])
                    answerJson.put(ctx.getString(R.string.question_5_ro), ans[4])
                }
                Const.LanguageCode.GERMANY -> {
                    answerJson.put(ctx.getString(R.string.question_1_de), ans[0])
                    answerJson.put(ctx.getString(R.string.question_2_de), ans[1])
                    answerJson.put(ctx.getString(R.string.question_3_de), ans[2])
                    answerJson.put(ctx.getString(R.string.question_4_de), ans[3])
                    answerJson.put(ctx.getString(R.string.question_5_de), ans[4])
                }
                else -> {
                    answerJson.put(ctx.getString(R.string.question_1_en), ans[0])
                    answerJson.put(ctx.getString(R.string.question_2_en), ans[1])
                    answerJson.put(ctx.getString(R.string.question_3_en), ans[2])
                    answerJson.put(ctx.getString(R.string.question_4_en), ans[3])
                    answerJson.put(ctx.getString(R.string.question_5_en), ans[4])
                }
            }

            return answerJson.toString()
        }

        fun prepareAnsImageJsonStr(questions: ArrayList<Question>): String{
            val answersImageJson = JSONArray()

            questions.forEach { question ->
                val answerImageJson = JSONArray()
                if(question.hasImage && question.images.isNotEmpty()) {
                    question.images.forEach { image ->
                        answerImageJson.put(image)
                    }
                }

                answersImageJson.put(answerImageJson)
            }

            return answersImageJson.toString()
        }
    }
}