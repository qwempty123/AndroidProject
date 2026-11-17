package com.example.coursemarket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CourseAdapter(
    private val onFavorite: (Course) -> Unit,
    private val onCart: (Course) -> Unit,
    private val onDetails: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private var items: List<Course> = emptyList()
    private val favoriteIds = mutableSetOf<Int>()
    private val cartIds = mutableSetOf<Int>()
    private val purchasedIds = mutableSetOf<Int>()

    fun submitItems(
        courses: List<Course>,
        favorites: Set<Int>,
        cart: Set<Int>,
        purchased: Set<Int>
    ) {
        items = courses.toList()
        favoriteIds.clear()
        favoriteIds.addAll(favorites)
        cartIds.clear()
        cartIds.addAll(cart)
        purchasedIds.clear()
        purchasedIds.addAll(purchased)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card = itemView.findViewById<MaterialCardView>(R.id.courseCard)
        private val category = itemView.findViewById<TextView>(R.id.courseCategory)
        private val icon = itemView.findViewById<TextView>(R.id.courseIcon)
        private val title = itemView.findViewById<TextView>(R.id.courseTitle)
        private val author = itemView.findViewById<TextView>(R.id.courseAuthor)
        private val description = itemView.findViewById<TextView>(R.id.courseDescription)
        private val meta = itemView.findViewById<TextView>(R.id.courseMeta)
        private val price = itemView.findViewById<TextView>(R.id.coursePrice)
        private val favorite = itemView.findViewById<MaterialButton>(R.id.courseFavorite)
        private val action = itemView.findViewById<MaterialButton>(R.id.courseAction)
        private val details = itemView.findViewById<MaterialButton>(R.id.courseDetails)

        fun bind(course: Course) {
            category.text = course.category.uppercase()
            icon.text = course.icon
            icon.setBackgroundColor(Color.parseColor(course.tint))
            title.text = course.title
            author.text = itemView.context.getString(R.string.course_author_format, course.author)
            description.text = course.description
            meta.text = "★ " + course.rating + "   •   " + course.duration +
                "   •   " + course.modules.size + " уроков"
            price.text = formatPrice(course.price)
            favorite.text = if (course.id in favoriteIds) "♥" else "♡"
            action.text = when {
                course.id in purchasedIds -> itemView.context.getString(R.string.course_learn)
                course.id in cartIds -> itemView.context.getString(R.string.course_in_cart)
                else -> itemView.context.getString(R.string.course_add_to_cart)
            }
            action.setOnClickListener { onCart(course) }
            favorite.setOnClickListener { onFavorite(course) }
            details.setOnClickListener { onDetails(course) }
            card.setOnClickListener { onDetails(course) }
        }

        private fun formatPrice(value: Int): String =
            String.format(java.util.Locale.US, "%,d ₽", value).replace(',', ' ')
    }
}
