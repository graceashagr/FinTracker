package com.example.core.data.sync

import java.util.Stack
import kotlin.math.max
import kotlin.math.min

fun isValidSudoku(board: Array<CharArray>): Boolean {
    val seen = HashSet<String>()
    for (r in 0 until 9) {
        for (c in 0 until 9) {
            val number = board[r][c]
            val box = (r / 3) * 3 + (c / 3)
            if (number.isDigit()) {
                val rowKey = "$r has $number"
                val colKey = "$c has $number"
                val boxKey = "$box has $number"
                if (!seen.add(rowKey) || !seen.add(colKey) || !seen.add(boxKey)) {
                    return false
                }
            }
        }
    }
    return true
}
//[100,4,200,1,3,2] ouput: 4 --> [1,2,3,4]
fun longestConsecutive(nums: IntArray): Int {
    var longestStreak = 0
    val numSet = HashSet<Int>()
    for(num in nums){
        numSet.add(num)
    }
    for (num in numSet){
        val hasPrevious = numSet.contains(num - 1)
        if (!hasPrevious) {
            var currentNum = num
            var currentStreak = 1
            while(numSet.contains(currentNum+1)){
                currentStreak++
                currentNum++
            }
            longestStreak = maxOf(longestStreak, currentStreak)
        }
    }
    return longestStreak
}

fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.length - 1
    while(left<right){
        while(left < right && !s[left].isLetterOrDigit()) left++
        while(left < right && !s[right].isLetterOrDigit()) right--
        if(left < right && s[left].lowercaseChar() != s[right].lowercaseChar()) return false
        left++
        right--
    }
    return true
}


fun twoSum(numbers: IntArray, target: Int): IntArray {
    var left = 0
    var right = numbers.size -1
    while(left < right) {
        val sum = numbers[left] + numbers[right]
        if(sum < target) left++
        else if (sum > target) right--
        else return intArrayOf(left + 1, right + 1)
    }
    return intArrayOf()
}


fun threeSum(nums: IntArray): List<List<Int>> {
 val triplet = mutableSetOf<List<Int>>()
    nums.sort()
    for (i in 0 until nums.size - 2){
        if(nums[i]>0) return triplet.toList()
        if (i > 0 && nums[i] == nums[i - 1]) continue
        var left = i+1
        var right = nums.size-1
        val target = -nums[i]
        while(left < right){
            val total = nums[left]+nums[right]
            if(total<target) left++
            else if (total> target) right--
            else {
                triplet.add(listOf(nums[i],nums[left],nums[right]))
                while (left < right && nums[left] == nums[left + 1]) left++
                while (left < right && nums[right] == nums[right - 1]) right--
                left++
                right--
            }
        }
    }
    return triplet.toList()
}

fun trap(height: IntArray): Int {
    val leftMax = intArrayOf()
    val rightMax = intArrayOf()
    var lMax = 0
    var rMax = 0
    var water = 0
    for (i in height.indices){
        if(i==0){
            lMax = max(height[i],0)
        } else {
            lMax = max(lMax,height[i-1])
        }
        leftMax[i] = lMax
    }
    for(i in height.size-1 downTo 0){
        if(i==0){
            rMax = max(height[i],0)
        } else {
            rMax = max(height[i-1],rMax)
        }
        rightMax[i] = rMax
    }
    for( i in height.indices){
        val limit = min(leftMax[i],rightMax[i])
        if(height[i]<limit){
            water += limit - height[i]
        }
    }
    return water
}

fun evalRPN(tokens: Array<String>): Int {
    val stack = Stack<Int>()
    for(token in tokens) {
        if (token == "+" || token == "-" || token == "*" || token == "/") {
            val first = stack.pop()
            val second = stack.pop()
            val result = when(token) {
                "*" -> second * first
                "+" -> second + first
                "-" -> second - first
                "/" -> second / first
                else -> throw IllegalArgumentException("Invalid operator")
            }
            stack.push(result)
        } else {
            stack.push(token.toInt())
        }
    }

    return stack.pop()
}



fun generateParenthesis(n: Int): List<String> {
    val output = mutableListOf<String>()
    backTrack(output,"",0,0,n)
    return output
}

private fun backTrack(list:MutableList<String>,str:String,open:Int,close:Int,max:Int){
    if(str.length==2*max){
        list.add(str)
        return
    }

    if(open<max){
        backTrack(list, str+"(",open+1,close,max)
    }
    if(close<open){
        backTrack(list,str+")",open,close+1,max)
    }
}

// [73,74,75,71,69,72,76,73]
fun dailyTemperatures(temperatures: IntArray): IntArray {
    val days = IntArray(temperatures.size)
    val stack = ArrayDeque<Int>()
    for (i in temperatures.indices){
        if(stack.isEmpty()){
            stack.add(temperatures[i])
        } else {
            if(stack.last() > temperatures[i]){
                stack.add(temperatures[i])
            } else {
                while(!stack.isEmpty() && stack.last() < temperatures[i]) {
                    val index = temperatures.indexOf(stack.last())
                    stack.removeLast()
                    days[index] = i - index
                }
                stack.add(temperatures[i])
            }
        }
    }
    return days
}

fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
    val sortedPos = position.sortedDescending()
    var fleet = 0
    var maxTime = 0
    for(pos in sortedPos){
        val time = (target - pos)/ speed[position.indexOf(pos)]
        if(time > maxTime){
            maxTime = time
            fleet++
        }
    }
    return fleet
}

fun lengthOfLongestSubstring(s: String): Int {
    val set = mutableSetOf<Char>()
    var left = 0
    var right = 0
    var maxLength = 0
    if( s.isEmpty() || s.length==1) return s.length
    for(char in s){
        if(!set.contains(char)){
            set.add(char)
            right++
        } else {
            while (set.contains(char)){
                set.remove(s[left])
                left++
            }
            set.add(char)
            right++
            val length = right - left + 1
            if(length > maxLength) maxLength = length
        }
    }
    return maxLength
}