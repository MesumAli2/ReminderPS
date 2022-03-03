package com.mesum.reminders

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mesum.reminders.databinding.FragmentStatisticsBinding

import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.reminderitem.*


class StatisticsFragment : Fragment() {
    private lateinit var  pieChart : PieChart


        private var _binding : FragmentStatisticsBinding? = null
        private val binding get () = _binding!!
    //private val viewModel by viewModels()
    private val viewModel: StatisticsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, StatisticsViewModel.Factory(activity.application))
            .get(StatisticsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            binding.apply {
                 this.viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        createPieChart()
        setupPieChart()
        loadPieChartData()
    }

    private fun setupPieChart() {
        //Center hole in the piechart
        pieChart.isDrawHoleEnabled = true
        //Pie chart uses percent values
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        //PieChart color inside the circle
        pieChart.setEntryLabelColor(Color.BLACK)
        //Text For the middle of the PieChart
        pieChart.centerText = "Tasks"
        pieChart.setCenterTextSize(24f)
        pieChart.description.isEnabled = false

        //Legend represents label text on the side of the graph
        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.textColor = Color.GRAY
        legend.isEnabled = true

    }

    private fun loadPieChartData() {
        //List containing PieChart  Data
        val pieChartData = mutableListOf<PieEntry>()
        viewModel.stats.observe(viewLifecycleOwner){
            //Adds completed and active Task entries into pieChart as PieEntry objects
            pieChartData.add(PieEntry(it.activeTasksPercent, "Active"))
            pieChartData.add(PieEntry(it.completedTasksPercent, "Completed"))

        val colors = mutableListOf<Int>()
        for (color: Int in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }

        for (color in ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color)
        }
        //Adds pieChartData into PieDataSet Object
        val pieChartDataSet = PieDataSet(pieChartData, "")
        pieChartDataSet.setColors(colors)
        //Adds pieChartDataSet which is PieDataSet object into PieData object that represents one dataSet
        val pieData = PieData(pieChartDataSet)
        pieData.setDrawValues(true)
        //Configures pieData to use the value percent formattermN
        pieData.setValueFormatter(PercentFormatter(pieChart))
            
        //Configures pieData to use desired textSize
        pieData.setValueTextSize(12f)
        //Configures pieData to use desired text Color
        pieData.setValueTextColor(Color.BLACK)
        //Assigns the pieChart in xml to PieData which represents PieDataSet
        pieChart.data = pieData
        pieChart.invalidate()
        //Adds animation to PieChart
        pieChart.animateY(1400, Easing.EaseInOutQuad)
}
    }

    private fun  createPieChart() {
        pieChart = binding.pieChartg
    }


}