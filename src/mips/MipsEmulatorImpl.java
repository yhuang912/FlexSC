package mips;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import gc.Boolean;

import oram.SecureMap;
import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.mips.memory.MemSetBuilder;
import com.appcomsci.mips.memory.MemorySet;
import com.appcomsci.mips.memory.MipsInstructionSet;
import com.appcomsci.sfe.common.Configuration;
import static com.appcomsci.mips.cpu.Utils.consistentHashString;
import static com.appcomsci.mips.cpu.Utils.consistentHash;
import static com.appcomsci.mips.cpu.Utils.makeInstructionSet;
import static com.appcomsci.mips.cpu.Utils.toStringSet;


import compiledlib.dov.CPU;
import compiledlib.dov.CpuImpl;
import compiledlib.dov.MEM;
import mips.EmulatorUtils;
import flexsc.CVCompEnv;
import flexsc.CompEnv;
import flexsc.CpuFcn;
// NEW import flexsc.CpuFcn;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

public class MipsEmulatorImpl<ET> implements MipsEmulator {
	static final boolean muteLoadInstructions = true;
	static final int THRESHOLD = 1024;
	static final int RECURSE_THRESHOLD = 512;
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final int REGISTER_SIZE = 32;
	
	/*
	 * XXInputIsRef indicates whether that user's inputs will fit into the two registers allocated to them.  
	 * I suppose it is possible they have 3 and 1 input values: that case isn't currently handled.
	 * If a user's value does not fit into the register space, the address is placed there (loadInputToRegisters).
	 * If the value does fit, and they only have one value, the second input value must be < 0, or it will 
	 * also be loaded.  
	 */
	static int stackFrameSize; 
	static int stackSize;
	
	static final int[][] aliceInput_2D_25 = {{0,11,10,9,35},{11,0,17,19,11},{10,17,0,7,29},{9,19,7,0,3},{35,11,29,3,0}};
	static final int[][] aliceInput_2D_100 = {{0,23,1,5,11,21,40,2,25,18},{23,0,31,26,15,20,16,24,31,9},{1,31,0,17,15,29,17,29,30,35},{5,26,17,0,37,19,12,25,18,40},{11,15,15,37,0,6,25,30,29,8},{21,20,29,19,6,0,17,19,16,15},{40,16,17,12,25,17,0,5,4,5},{2,24,29,25,30,19,5,0,33,17},{25,31,30,18,29,16,4,33,0,1},{18,9,35,40,8,15,5,17,1,0}};
	
	static final int[] aliceInputSortedArray_75 = {6,18,35,52,58,66,71,89,95,120,121,122,124,147,148,173,174,183,189,227,236,245,245,251,254,261,262,262,270,274,281,283,302,311,316,316,321,337,347,365,367,379,383,394,421,449,452,467,468,485,490,491,515,534,557,584,586,595,601,606,619,620,626,638,665,676,677,680,694,701,715,728,730,733,745};
	static final int[] aliceInputSortedArray_50 = {6,37,58,59,78,105,125,138,141,144,148,165,179,197,219,237,240,252,286,287,294,345,348,351,359,363,364,368,368,368,372,382,383,383,389,389,409,439,441,448,452,464,465,468,472,481,486,487,487,491};
	static final int[] aliceInputSortedArray_30 = {4,5,16,34,36,47,53,59,60,78,82,99,102,133,133,142,148,154,158,171,180,191,195,203,205,238,247,249,268,268};
	static final int[] aliceInputSortedArray_20 = {4,33,54,57,65,70,75,83,111,113,118,124,129,132,144,155,170,175,187,189};
	static final int[] aliceInputSortedArray_300 = {4,12,27,31,42,47,48,49,53,54,55,63,65,70,104,105,106,111,128,142,151,153,172,205,205,216,218,220,246,248,256,272,279,280,341,371,373,376,379,382,386,395,396,402,429,432,438,443,444,464,471,495,500,502,506,512,526,533,541,559,574,578,587,594,601,605,622,625,663,666,667,676,679,688,695,697,707,713,714,719,726,737,740,764,769,776,776,784,785,795,800,819,823,828,830,841,845,852,862,863,867,873,876,881,885,888,898,904,905,907,918,928,940,952,957,962,974,1020,1030,1044,1053,1063,1077,1092,1107,1107,1109,1112,1128,1131,1158,1174,1179,1194,1210,1233,1253,1254,1257,1263,1263,1264,1277,1281,1295,1302,1311,1319,1335,1346,1363,1378,1400,1406,1426,1432,1444,1447,1451,1480,1493,1502,1507,1528,1540,1556,1561,1575,1576,1577,1625,1630,1638,1642,1675,1722,1727,1731,1742,1754,1754,1769,1781,1782,1790,1796,1796,1798,1799,1820,1832,1836,1837,1851,1870,1875,1877,1879,1881,1885,1903,1913,1925,1938,1945,1957,1958,1985,1985,1991,2007,2042,2050,2050,2056,2086,2101,2126,2131,2133,2148,2154,2160,2179,2189,2192,2194,2210,2213,2235,2238,2247,2261,2262,2297,2326,2330,2332,2340,2343,2350,2354,2373,2377,2379,2386,2389,2391,2401,2407,2429,2449,2458,2465,2471,2493,2515,2516,2518,2521,2531,2531,2544,2547,2552,2574,2581,2593,2597,2602,2627,2648,2663,2676,2678,2681,2688,2709,2725,2755,2761,2777,2780,2782,2784,2794,2809,2813,2813,2831,2846,2855,2865,2898,2937,2944,2949,2958,2959,2965};
	static final int[] aliceInputSortedArray_500 = {4,13,18,22,33,42,60,60,66,75,80,81,84,87,91,94,103,106,107,112,119,131,137,145,146,158,179,180,186,188,209,210,214,219,227,236,236,263,269,274,276,288,289,291,292,299,318,320,332,340,369,375,376,379,420,424,427,432,447,479,482,490,492,504,517,525,528,530,543,561,604,605,642,646,649,656,716,734,777,796,806,813,822,823,831,841,844,846,850,865,868,904,922,935,962,986,993,997,1010,1024,1036,1045,1058,1063,1089,1101,1106,1107,1130,1134,1167,1182,1187,1189,1194,1199,1206,1209,1223,1226,1235,1247,1258,1259,1260,1279,1307,1342,1345,1345,1345,1348,1350,1364,1370,1378,1398,1404,1413,1423,1425,1432,1443,1447,1462,1467,1485,1503,1504,1507,1509,1513,1520,1537,1551,1561,1565,1569,1595,1624,1629,1641,1645,1662,1680,1715,1720,1728,1737,1739,1740,1741,1755,1757,1772,1774,1776,1780,1791,1796,1796,1808,1810,1812,1826,1857,1858,1894,1895,1905,1910,1917,1960,1968,1975,1984,1991,2015,2021,2041,2058,2060,2066,2093,2116,2121,2130,2144,2149,2161,2182,2183,2186,2210,2215,2239,2243,2272,2291,2297,2298,2326,2333,2353,2386,2413,2415,2426,2429,2429,2430,2443,2446,2483,2519,2519,2556,2580,2599,2612,2612,2612,2633,2634,2638,2641,2647,2675,2682,2685,2692,2700,2721,2749,2769,2770,2774,2779,2791,2800,2804,2804,2811,2820,2831,2852,2855,2861,2867,2869,2877,2878,2911,2926,2933,2978,2991,2992,3004,3045,3057,3069,3073,3074,3079,3083,3093,3106,3118,3124,3133,3137,3147,3159,3170,3172,3178,3183,3187,3189,3197,3203,3205,3229,3236,3256,3257,3263,3268,3281,3283,3284,3289,3290,3300,3313,3317,3318,3324,3334,3358,3359,3370,3376,3417,3426,3427,3447,3468,3473,3479,3486,3503,3503,3542,3543,3551,3552,3556,3564,3578,3597,3608,3611,3613,3614,3629,3635,3639,3652,3659,3661,3675,3676,3683,3689,3696,3696,3701,3707,3709,3743,3758,3759,3802,3804,3838,3853,3854,3873,3877,3896,3912,3932,3952,3964,3980,3981,3984,3988,3994,4029,4032,4037,4050,4067,4081,4082,4086,4099,4105,4106,4109,4113,4128,4143,4168,4193,4193,4196,4203,4227,4233,4237,4243,4252,4263,4264,4266,4268,4276,4279,4289,4298,4299,4308,4317,4325,4326,4330,4335,4350,4365,4381,4382,4383,4388,4391,4394,4415,4422,4423,4431,4438,4446,4448,4482,4490,4495,4507,4513,4537,4545,4546,4556,4556,4572,4594,4601,4607,4612,4619,4621,4622,4623,4631,4638,4645,4652,4669,4671,4680,4687,4703,4712,4712,4715,4715,4716,4723,4733,4737,4739,4742,4769,4787,4788,4789,4814,4821,4845,4852,4852,4861,4863,4883,4884,4887,4908,4912,4914,4941,4958,4960,4962,4983,4985,4989,4992,4999};
	
	static final int[] bobInputSortedArray_75 = {38,58,65,69,70,74,86,86,107,113,166,177,184,217,225,233,244,246,249,252,262,265,266,269,281,290,295,314,316,331,338,345,347,369,370,378,390,390,393,393,410,422,431,435,438,454,466,474,503,525,533,538,539,545,546,575,580,581,583,585,608,617,617,640,642,646,648,663,671,685,687,697,711,717,748};
	static final int[] bobInputSortedArray_50 = {5,20,28,42,47,50,55,75,88,91,104,104,162,188,191,192,199,218,236,236,253,273,298,301,314,324,331,338,346,349,358,361,369,374,386,393,398,400,412,413,424,442,445,452,457,459,467,468,477,484};
	static final int[] bobInputSortedArray_20  ={5,19,21,38,46,60,64,65,72,73,77,78,80,120,144,148,156,175,190,196};
	static final int[] bobInputSortedArray_300 = {7,37,55,58,59,70,83,94,107,121,134,164,165,173,175,177,180,188,195,205,210,210,214,224,229,272,301,310,316,316,330,337,344,346,394,413,415,442,450,459,459,465,471,483,490,507,517,548,552,598,612,630,640,654,656,664,670,677,677,681,716,717,722,727,730,761,771,771,783,791,793,796,796,801,808,809,816,818,825,828,844,845,860,863,864,869,890,897,904,926,932,937,940,943,947,947,951,952,952,981,982,982,985,1004,1015,1035,1051,1074,1086,1090,1126,1130,1133,1142,1169,1178,1210,1216,1221,1222,1238,1239,1267,1267,1277,1291,1302,1307,1311,1324,1340,1342,1362,1371,1372,1373,1381,1381,1385,1388,1412,1413,1425,1439,1451,1453,1465,1465,1480,1485,1497,1502,1507,1519,1531,1560,1572,1583,1589,1605,1624,1625,1626,1635,1645,1675,1681,1717,1723,1729,1736,1741,1755,1778,1790,1790,1794,1814,1828,1831,1839,1853,1874,1898,1911,1921,1929,1934,1935,1944,1956,1970,1998,2014,2023,2031,2036,2038,2055,2065,2074,2103,2125,2128,2131,2134,2135,2143,2172,2182,2186,2187,2189,2207,2208,2209,2212,2222,2233,2248,2255,2258,2269,2273,2279,2285,2288,2293,2294,2296,2308,2310,2331,2339,2342,2357,2362,2377,2384,2385,2386,2390,2414,2446,2446,2448,2454,2462,2470,2483,2496,2509,2523,2528,2542,2571,2576,2599,2600,2607,2609,2616,2632,2649,2654,2664,2667,2672,2697,2698,2708,2722,2731,2745,2753,2764,2775,2781,2790,2831,2837,2838,2840,2858,2875,2876,2887,2888,2889,2905,2909,2925,2938,2940,2942,2943,2983,2989,2993,2995};  
	//static final int[] bobInputSortedArray_500 = {1,3,7,7,9,11,15,20,23,34,36,45,63,66,78,90,154,164,198,200,211,234,244,245,252,261,263,270,275,290,292,294,296,300,300,325,337,338,338,357,365,368,371,376,380,387,413,449,471,484,491,495,503,505,507,508,516,544,561,601,602,605,612,630,649,672,679,691,711,723,752,752,753,754,772,781,794,808,814,830,835,842,860,875,881,886,886,901,912,950,950,959,962,963,969,984,988,1018,1021,1024,1048,1057,1066,1069,1075,1076,1080,1086,1098,1111,1131,1170,1182,1194,1199,1220,1232,1241,1242,1247,1252,1274,1278,1278,1291,1300,1342,1353,1378,1395,1398,1400,1425,1427,1454,1458,1458,1471,1479,1495,1495,1498,1505,1509,1516,1523,1527,1541,1551,1592,1596,1604,1610,1613,1619,1653,1660,1666,1668,1668,1669,1673,1678,1678,1687,1688,1695,1701,1715,1716,1734,1751,1758,1761,1763,1770,1787,1799,1800,1804,1819,1822,1830,1857,1863,1870,1898,1899,1899,1914,1918,1925,1928,1941,1977,1979,1979,1991,1995,2018,2025,2046,2056,2056,2060,2067,2068,2087,2093,2099,2100,2102,2107,2116,2116,2119,2120,2126,2129,2134,2135,2170,2170,2177,2186,2187,2188,2189,2194,2194,2198,2199,2237,2239,2255,2258,2264,2272,2277,2286,2299,2300,2311,2315,2319,2340,2348,2349,2362,2381,2409,2411,2412,2418,2425,2442,2442,2452,2464,2467,2471,2472,2476,2496,2511,2516,2541,2556,2572,2574,2581,2589,2594,2605,2623,2638,2638,2642,2651,2658,2668,2670,2672,2685,2686,2687,2691,2691,2699,2708,2710,2728,2759,2760,2772,2773,2774,2776,2789,2789,2800,2834,2868,2922,2938,2939,2947,2958,2971,2972,2973,2978,2984,3000,3005,3010,3013,3023,3023,3044,3048,3057,3082,3095,3100,3102,3119,3123,3136,3141,3141,3141,3150,3159,3171,3172,3210,3213,3219,3223,3231,3277,3293,3298,3306,3345,3363,3367,3373,3380,3391,3395,3435,3442,3449,3455,3455,3476,3480,3481,3481,3485,3502,3503,3510,3519,3533,3550,3566,3568,3569,3570,3584,3590,3609,3629,3636,3639,3648,3656,3665,3668,3692,3703,3722,3722,3759,3781,3807,3823,3829,3864,3877,3880,3889,3893,3927,3954,3956,3960,3973,3979,3994,4006,4006,4018,4022,4026,4043,4047,4057,4071,4073,4078,4105,4109,4111,4125,4174,4187,4207,4214,4216,4237,4239,4247,4250,4262,4274,4282,4283,4286,4308,4316,4326,4359,4380,4380,4394,4425,4434,4441,4490,4495,4523,4530,4532,4536,4543,4547,4561,4567,4572,4582,4601,4607,4617,4623,4625,4631,4633,4644,4650,4664,4676,4693,4699,4712,4722,4749,4768,4776,4788,4791,4801,4804,4824,4827,4829,4852,4860,4863,4871,4884,4885,4886,4917,4920,4922,4923,4925,4934,4939,4943,4950,4950,4959,4971,4984,4987};
	
	static final int[] aliceInputUnsortedArray_11 = {20,5,10,4,30,10,32,10,3,22,24};
	
	static final String aliceInputString_9 = "aaaaaaaaa";
	static final String bobInputString_9 = "bbaaabaab";
	
	static int[] aliceInputArray;
	static int[][] aliceInput_2D;
	static int[] bobInputArray;
	static String aliceInputString;
	static String bobInputString;
	static TreeMap<Long, boolean[]> aliceFuncMap;
	static int aliceFuncAddress;

	// Should we blither about missing CPUs?
	static final boolean blither = false;
	
	protected LocalConfiguration config;
	
	private MipsEmulatorImpl(LocalConfiguration config) throws Exception {
		this.config = config;
		setRunParameters(config);
		
	}

	private void setRunParameters(LocalConfiguration config) throws Exception{



        if (config.getBinaryFileName().equals("djikstra")|| config.getBinaryFileName().equals("bubble_sort")
				|| config.getBinaryFileName().equals("binary_search")){
			config.setBobInputSize(0);
		}	
		if (config.getBinaryFileName().equals("djikstra")){
			if (config.getAliceInputSize() == 25){
				stackFrameSize = 66;
				aliceInput_2D = aliceInput_2D_25;
			}
			else if (config.getAliceInputSize() == 100){
				stackFrameSize = 128;
				aliceInput_2D = aliceInput_2D_100;
			}
		}else if (config.getBinaryFileName().equals("func_point")) {
            Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
            DataSegment aliceInstructions = rdr.getInstructions(config.getAliceFuncInput());
            aliceFuncMap = aliceInstructions.getDataAsBooleanMap();
            //note, casting an address to int. 
            aliceFuncAddress = (int) aliceInstructions.getStartAddress();
            config.setAliceFuncSize(aliceInstructions.getDataLength());
            config.setMultipleBanks(false);
        	if (config.getAliceInputSize() == 20){
				aliceInputArray = aliceInputSortedArray_20;
				bobInputArray = bobInputSortedArray_20;
			}    
        	stackFrameSize = 128;
		}
		else if (config.getBinaryFileName().equals("bubble_sort")){
			if (config.getAliceInputSize() == 11){
				stackFrameSize = 40;
				aliceInputArray = aliceInputUnsortedArray_11;
			}
		}
		else if (config.getBinaryFileName().equals("set_intersection")){
			if (config.getAliceInputSize() == 300){
				stackFrameSize = 32;
				aliceInputArray = aliceInputSortedArray_300;
			}
			if (config.getBobInputSize() == 300)
				bobInputArray = bobInputSortedArray_300;
			if (config.getAliceInputSize() == 75){
				stackFrameSize = 32;
				aliceInputArray = aliceInputSortedArray_75;
			}
			if (config.getBobInputSize() == 75)
				bobInputArray = bobInputSortedArray_75;			
			
			if (config.getAliceInputSize() == 50){
				stackFrameSize = 32;
				aliceInputArray = aliceInputSortedArray_50;
			}
			if (config.getBobInputSize() == 50)
				bobInputArray = bobInputSortedArray_50;
			
			if (config.getAliceInputSize() == 20){
				stackFrameSize = 32;
				aliceInputArray = aliceInputSortedArray_20;
			}
			if (config.getBobInputSize() == 20)
				bobInputArray = bobInputSortedArray_20;
		}	
		else if (config.getBinaryFileName().equals("binary_search")){
			if (config.getAliceInputSize() == 30)
				aliceInputArray = aliceInputSortedArray_30;	
			else if (config.getAliceInputSize() == 50)
				aliceInputArray = aliceInputSortedArray_50;
			else if (config.getAliceInputSize() == 300)
				aliceInputArray = aliceInputSortedArray_300;
			else if (config.getAliceInputSize() == 20)
				aliceInputArray = aliceInputSortedArray_20;
			else if (config.getAliceInputSize() == 500)
				aliceInputArray = aliceInputSortedArray_500;
			stackFrameSize = 32;	
		}
		else if (config.getBinaryFileName().equals("lcs")){
			if (config.getAliceInputSize() == 9){
				aliceInputString = aliceInputString_9;
				stackFrameSize = 408;
				config.setAliceInputSize(config.getAliceInputSize()/4 + 1); 
			}
			if (config.getBobInputSize() == 9){
				bobInputString = bobInputString_9;
				config.setBobInputSize(config.getBobInputSize()/4 + 1);
			}
			aliceInputArray = EmulatorUtils.castStringToIntArray(aliceInputString);
			bobInputArray = EmulatorUtils.castStringToIntArray(bobInputString);
		}
		else{
			System.out.println("no setting for stackFrameSize.  exiting.");
			System.exit(2);
		}
		stackFrameSize = stackFrameSize / 4 ;
		if (config.getBinaryFileName().equals("lcs"))
			stackSize = stackFrameSize + (config.getAliceInputSize()/4) + (config.getBobInputSize()/4) + 10;
		else
			stackSize = stackFrameSize + config.getAliceInputSize() + config.getBobInputSize() + 8;
		
	}
	
	
	private static class LocalConfiguration extends Configuration {
		
		private String binaryFileName;
		private Mode mode = Mode.VERIFY;
		private int aliceInputSize;
		private int bobInputSize;
		private int aliceIntInput;
		private int bobIntInput;
		private int aliceIntInput2;
		private int bobIntInput2;
		private String aliceFuncInput;
		private int aliceFuncSize;
				
		public static final String MODE_PROPERTY = "mode";
		public static final String DEFAULT_MODE = "VERIFY";
		public static final String BINARY_NAME_PROPERTY = "binary_name";
		public static final String DEFAULT_PROG = "djikstra";
		public static final String ALICE_INPUT_SIZE_PROPERTY = "alice_input_size";
		public static final String DEFAULT_ALICE_INPUT_SIZE = "0";
		public static final String BOB_INPUT_SIZE_PROPERTY = "bob_input_size";
		public static final String DEFAULT_BOB_INPUT_SIZE = "0";
		public static final String ALICE_INPUT_PROPERTY = "alice_integer_input";
		public static final String DEFAULT_ALICE_INPUT = "-1";
		public static final String ALICE_INPUT2_PROPERTY = "alice_integer_input2";
		public static final String DEFAULT_ALICE_INPUT2 = "-1";
		public static final String ALICE_FUNC_INPUT_PROPERTY = "alice_func_input";
        public static final String ALICE_FUNC_SIZE_PROPERTY = "alice_func_size";
        public static final String DEFAULT_ALICE_FUNC_SIZE = "0";
        public static final String DEFAULT_ALICE_FUNC_INPUT = "aliceInput_set_intersection";
		public static final String BOB_INPUT_PROPERTY = "bob_integer_input";
		public static final String DEFAULT_BOB_INPUT = "2";
		public static final String BOB_INPUT2_PROPERTY = "bob_integer_input2";
		public static final String DEFAULT_BOB_INPUT2 = "4";
		
		protected LocalConfiguration() throws IOException {
			super();
			String tmp = null;
			try {
				tmp = getProperties().getProperty(MODE_PROPERTY, DEFAULT_MODE);
				mode = Mode.valueOf(tmp);
				tmp = getProperties().getProperty(BINARY_NAME_PROPERTY, DEFAULT_PROG);
				binaryFileName = tmp;
				tmp = getProperties().getProperty(ALICE_INPUT_SIZE_PROPERTY, DEFAULT_ALICE_INPUT_SIZE);
				aliceInputSize = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(BOB_INPUT_SIZE_PROPERTY, DEFAULT_BOB_INPUT_SIZE);
				bobInputSize = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(ALICE_INPUT_PROPERTY, DEFAULT_ALICE_INPUT);
				aliceIntInput = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(BOB_INPUT_PROPERTY, DEFAULT_BOB_INPUT);
				bobIntInput = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(ALICE_INPUT2_PROPERTY, DEFAULT_ALICE_INPUT2);
				aliceIntInput2 = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(ALICE_FUNC_INPUT_PROPERTY, DEFAULT_ALICE_FUNC_INPUT);
				aliceFuncInput = tmp;
                tmp = getProperties().getProperty(ALICE_FUNC_SIZE_PROPERTY, DEFAULT_ALICE_FUNC_SIZE);
                aliceFuncSize = Integer.parseInt(tmp);
				tmp = getProperties().getProperty(BOB_INPUT2_PROPERTY, DEFAULT_BOB_INPUT2);
				bobIntInput2 = Integer.parseInt(tmp);
				
			} catch(Exception e) {
				System.err.println("No such mode: " + tmp);
			}
		}
		
		@SuppressWarnings("unused")
		protected LocalConfiguration(LocalConfiguration that) throws IOException {
			super(that);
			this.setMode(that.getMode());
			this.setBinaryFileName(that.getBinaryFileName());
		}
		
		public int getAliceInputSize() {return aliceInputSize;}
		public int getBobInputSize() {return bobInputSize;}
		public void setAliceInputSize(int x) { aliceInputSize = x; }
		public void setBobInputSize(int x) { bobInputSize = x; }
		public int getAliceIntInput(){ return aliceIntInput; }
		public int getBobIntInput(){ return bobIntInput; }
		public int getAliceIntInput2(){ return aliceIntInput2; }
		public String getAliceFuncInput() { return aliceFuncInput; }
        public int getAliceFuncSize() {return aliceFuncSize; }
        public void setAliceFuncSize(int x) { aliceFuncSize = x; }
		public int getBobIntInput2(){ return bobIntInput2; }
		public void setBinaryFileName(String fileName) {
			binaryFileName = fileName;
		}

		public String getBinaryFileName() {
			return binaryFileName;
		}

		/**
		 * @return the mode
		 */
		public Mode getMode() {
			return mode;
		}

		/**
		 * @param mode the mode to set
		 */
		public void setMode(Mode mode) {
			this.mode = mode;
		}
	}
	

	private class MipsParty<T> {
		List<MemorySet<T>> sets;
		DataSegment instData; 
		DataSegment memData;
		int pcOffset; 
		int dataOffset; 
		IntegerLib<T> lib;
		public MipsParty(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			this.sets = sets;
			this.instData = instData;
			this.memData = memData;
			this.pcOffset = pcOffset;
			this.dataOffset = dataOffset;
		}
		public SecureArray<T> reg;
		public void mainloop(CompEnv<T> env) throws Exception{
			//testInstruction(env);
			lib = new IntegerLib<T>(env);
			CpuFcn<T> defaultCpu = new CpuImpl<T>(env);
			MEM<T> mem = new MEM<T>(env);
			reg = loadInputsToRegister(env, this.dataOffset);
			
			loadCpus(sets, env);

			SecureMap<T> singleInstructionBank = null;

			if (!config.isMultipleBanks()){
				singleInstructionBank = loadInstructionsSingleBank(env, instData);				
			}
			loadInstructionsMultiBanks(env, singleInstructionBank, sets);
			SecureArray<T> memBank = getMemory(env, memData);

			T[] pc = lib.toSignals(pcOffset, WORD_SIZE);
			T[] newInst = lib.toSignals(0, WORD_SIZE);
			boolean testHalt;
			int count = 0;
			int numBanksWithMem=0;
			//if (!config.isMultipleBanks())
				//EmulatorUtils.printOramBank(singleInstructionBank, lib, 60);
			long startTime = System.nanoTime();
			long fetchTime = 0, fetchAnd =0;
			long fetchTimeStamp = 0, fetchAndStamp = 0;
			long loadStoreTime = 0, loadStoreAnd = 0;
			long loadStoreTimeStamp = 0, loadStoreAndStamp=0;
			long cpuTime = 0, cpuAnd = 0;
			long cpuTimeStamp = 0, cpuAndStamp = 0;
			MemorySet<T> currentSet = sets.get(0);
			SecureMap<T> currentBank;
			dataOffset -= (stackSize*4);
			while (true) {
				currentBank = currentSet.getOramBank().getMap();
				EmulatorUtils.print("count: " + count + "\nexecution step: " + currentSet.getExecutionStep(), lib, false);
				count++;
				//if (count % 100 == 0)  System.out.println("count: " + count);
				//if (config.isMultipleBanks())
					//currentSet.getOramBank().getMap().print();
				if (config.isMultipleBanks())
					pcOffset = (int) currentSet.getOramBank().getMinAddress();
				fetchTimeStamp = System.nanoTime();
				if(env.m == Mode.VERIFY)
					fetchAndStamp = ((CVCompEnv)(env)).numOfAnds;
				newInst = mem.getInst(currentBank, pc, pcOffset);
				fetchTime += System.nanoTime() - fetchTimeStamp;
				if(env.m == Mode.VERIFY)
					fetchAnd += ((CVCompEnv)(env)).numOfAnds - fetchAndStamp;

				
				//newInst = mem.getInst(singleInstructionBank, pc, pcOffset); 
				
				if (currentSet.isUsesMemory()){
					numBanksWithMem++;
					loadStoreTimeStamp = System.nanoTime();
					if(env.m == Mode.VERIFY)
						loadStoreAndStamp = ((CVCompEnv)(env)).numOfAnds;
					mem.func(reg, memBank, newInst, dataOffset);
					loadStoreTime += System.nanoTime() - loadStoreTimeStamp;
					if(env.m == Mode.VERIFY)
						loadStoreAnd += ((CVCompEnv)(env)).numOfAnds - loadStoreAndStamp;

				}

				testHalt = testTerminate(reg, newInst, lib);

				if (testHalt)
					break;

				EmulatorUtils.printBooleanArray("newInst", newInst, lib);


				CpuFcn<T> cpu = currentSet.getCpu();
				cpuTimeStamp = System.nanoTime();
				if(env.m == Mode.VERIFY)
					cpuAndStamp = ((CVCompEnv)(env)).numOfAnds;

				if(cpu == null  || !config.isMultipleBanks())
					pc = defaultCpu.function(reg, newInst, pc, null);
				else
					pc = cpu.function(reg, newInst, pc, null);
				cpuTime += System.nanoTime() - cpuTimeStamp;
				if(env.m == Mode.VERIFY)
					cpuAnd += ((CVCompEnv)(env)).numOfAnds - cpuAndStamp;


				EmulatorUtils.printRegisters(reg, lib);

				EmulatorUtils.printBooleanArray("PC", pc, lib);
				EmulatorUtils.print(pcOffset+"", lib);
				EmulatorUtils.print(currentSet.getOramBank().getMinAddress()+"", lib);

				currentSet = currentSet.getNextMemorySet();
			}
			float runTime =  ((float)(System.nanoTime() - startTime))/ 1000000000;
			float cpuTimeFl = ((float)cpuTime) / 1000000000;
			float fetchTimeFl = ((float)fetchTime) / 1000000000;
			float loadStoreTimeFl = ((float)loadStoreTime) / 1000000000;
			synchronized (MipsParty.class) {
				System.out.println(env.getParty());
				System.out.println("Count:"  + count);
				System.out.println("Run time: " + runTime);
				System.out.println("Average time / instruction: " + runTime / count );
				System.out.println("Time in CPU: " + cpuTimeFl);
				System.out.println("Average CPU time: " + cpuTimeFl / count);
				System.out.println("Time in instruction fetch: " + fetchTimeFl);
				System.out.println("Average fetch time: " + fetchTimeFl / count);
				System.out.println("Time in loadStore: " + loadStoreTimeFl);
				System.out.println("Average loadStore time: " + loadStoreTimeFl / count);
			
				System.out.println("Average CPU #ANDs: " + cpuAnd / count);
				System.out.println("Average fetch #ANDS: " + fetchAnd / count);
				System.out.println("Average loadStore #ANDS: " + loadStoreAnd/count);
			}
			EmulatorUtils.printBooleanArray("Rsult", reg.read(lib.toSignals(2, 32)), lib, false);
			System.out.println("numBanks with memory: " + numBanksWithMem);
			
		}
		
		private void loadCpus(List<MemorySet<T>> sets, CompEnv<T>env) {
			if(!config.isMultipleBanks()) {
				System.out.println("Not loading CPUs for single bank execution");
				return;
			}
			//System.out.println("Entering loadCpus");
			// Uses arcane knowledge. FIXME
			String packageName = CPU.class.getPackage().getName();
			String classNameRoot = "Cpu";
			for(MemorySet<T>s:sets) {
				CpuFcn<T> cpu = s.findCpu(env, packageName, classNameRoot, true);
				if(cpu == null && blither) {
					System.err.println("Could not find cpu for: [" +
							consistentHash(toStringSet(makeInstructionSet(s))) +
							"] " + consistentHashString(toStringSet(makeInstructionSet(s)))
							);
				}
			}
			//System.out.println("Exiting loadCpus");
		}

		public void testInstruction (CompEnv<T> env) throws Exception {

			SecureArray<T> reg = new SecureArray<T>(env, REGISTER_SIZE, WORD_SIZE);
			//int inst = 		0b00000000000000110001011011000010; //SRL
			int inst = 		0b00000000000000100001000001000011; //OR
			int rsCont = 	0b00000000000000000000000000000101;
			int rtCont = 	0b00000000000000000000000000000101;
			//int rdCont = 	0b00000000000000000000000000000000;
			T[] rs = env.inputOfAlice(Utils.fromInt(2, reg.lengthOfIden));
			T[] rt = env.inputOfAlice(Utils.fromInt(2, reg.lengthOfIden));
			//Boolean[] rd = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
			T[] rsContent = env.inputOfAlice(Utils.fromInt(rsCont, WORD_SIZE));
			T[] rtContent = env.inputOfAlice(Utils.fromInt(rtCont, WORD_SIZE));
			//Boolean[] rdContent = env.inputOfAlice(Utils.fromInt(rdCont, WORD_SIZE));
			reg.write(rs, rsContent);
			reg.write(rt, rtContent);
			//reg.write(rd, rdContent);
			env.flush();

			CPU<T> cpu = new CPU<T>(env);
			IntegerLib<T> lib = new IntegerLib<T>(env);
			T[] pc; 
			pc = cpu.function(reg, env.inputOfAlice(Utils.fromInt(inst, 32)), env.inputOfAlice(Utils.fromInt(0,32)));

			String output = "";
			for (int i = 31 ; i >= 26;  i--){
				if ((inst & (1 << i)) != 0)
					output += "1";
				else 
					output += "0";
			}
			output += "|";
			for (int i = 25 ; i >= 21;  i--){
				if ((inst & (1 << i)) != 0)
					output += "1";
				else 
					output += "0";
			}
			output += "|";
			for (int i = 20 ; i >= 16;  i--){
				if ((inst & (1 << i)) != 0)
					output += "1";
				else 
					output += "0";
			}
			output += "|";
			for (int i = 15 ; i >= 11;  i--){
				if ((inst & (1 << i)) != 0)
					output += "1";
				else 
					output += "0";
			}
			output += "|";
			for (int i = 10 ; i >= 0;  i--){
				if ((inst & (1 << i)) != 0)
					output += "1";
				else 
					output += "0";
			}
			if(lib.getEnv().getParty() == Party.Alice)
				System.out.println("testing instruction: " + output);
			EmulatorUtils.printRegisters(reg, lib);	
			if(lib.getEnv().getParty() == Party.Alice)
				EmulatorUtils.printBooleanArray("PC", pc, lib);
		}
		private boolean testTerminate(SecureArray<T> reg, T[] ins, IntegerLib<T> lib) {
			// Look for branch to here.  There are several ways to code this.
			// Gcc and cousins use BEQ $0,$0,-1
			// 0x1000ffff = 0b000100 00000 00000 1111111111111111
			T eq = lib.eq(ins, lib.toSignals(0x1000ffff, 32));
			// Look for jr $31 where $31 contains zero
			// 0x03e00008 = 0b000000 11111 0000000000 00000 001000
			T eq1 = lib.eq(ins, lib.toSignals(0x03e00008, 32));
			T eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
			eq1 = lib.and(eq1, eq2);
			eq = lib.or(eq,  eq1);
			T[] res = lib.getEnv().newTArray(1);
			res[0] = eq1;
			return lib.declassifyToBoth(res)[0]; 
		}



		// integer or pointer to array.

		// we are doing point to array

		// load right pointer to correct register



		// execute program that takes as input a pointer to function

		// along with other inputs

		// program takes pointer to set intersection and 4 inputs



		// alice_input()



		private SecureArray<T> loadInputsToRegister(CompEnv<T> env, int dataOffset)
				throws Exception {
			int aliceReg = 4; 
			int bobReg = 5;
			// inital registers are all 0's. no need to set value.
			SecureArray<T> oram = new SecureArray<T>(env,REGISTER_SIZE, WORD_SIZE);
			for(int i = 0; i < REGISTER_SIZE; ++i)
				oram.write(env.inputOfAlice(Utils.fromInt(i, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(0, WORD_SIZE)));
			
			//REGISTER 4

            if (config.getBinaryFileName().equals("func_point")) {
                oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
                        env.inputOfAlice(Utils.fromInt(aliceFuncAddress, WORD_SIZE)));
            } else if (config.getAliceInputSize() > 2) {
                oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
                        env.inputOfAlice(Utils.fromInt(dataOffset - (4 * (config.getAliceInputSize() + config.getBobInputSize())), WORD_SIZE)));
            }
            // we assume at least one input to the program!
            else
                oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
                        env.inputOfAlice(Utils.fromInt(config.getAliceIntInput(), WORD_SIZE)));

			//REGISTER 5

            if (config.getBinaryFileName().equals("func_point"))
                oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
                        env.inputOfAlice(Utils.fromInt(dataOffset - (4*(config.getAliceInputSize() + config.getBobInputSize())), WORD_SIZE)));


			else if (config.getBinaryFileName().equals("set_intersection") 
					|| config.getBinaryFileName().equals("lcs"))
				oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(dataOffset - (4*config.getBobInputSize()), WORD_SIZE)));
			else if (config.getBinaryFileName().equals("bubble_sort") 
					|| config.getBinaryFileName().equals("binary_search"))
				oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(config.getAliceInputSize(), WORD_SIZE)));
			else  
				oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(config.getBobIntInput(), WORD_SIZE)));

			//REGISTER 6
            if (config.getBinaryFileName().equals("func_point"))
            	oram.write(env.inputOfAlice(Utils.fromInt(6, oram.lengthOfIden)),
            			env.inputOfAlice(Utils.fromInt(dataOffset - (4*(config.getBobInputSize())), WORD_SIZE)));

            if (config.getBinaryFileName().equals("djikstra"))
            	oram.write(env.inputOfAlice(Utils.fromInt(6, oram.lengthOfIden)),
            			env.inputOfAlice(Utils.fromInt(config.getBobIntInput2(), WORD_SIZE)));
            else if (config.getBinaryFileName().equals("set_intersection"))
            	oram.write(env.inputOfAlice(Utils.fromInt(6, oram.lengthOfIden)),
            			env.inputOfAlice(Utils.fromInt(config.getAliceInputSize(), WORD_SIZE)));
            else if (config.getBinaryFileName().equals("binary_search"))
            	oram.write(env.inputOfAlice(Utils.fromInt(6, oram.lengthOfIden)),
            			env.inputOfAlice(Utils.fromInt(config.getBobIntInput(), WORD_SIZE)));
				
			//REGISTER 7
				if (config.getBinaryFileName().equals("set_intersection"))
					oram.write(env.inputOfAlice(Utils.fromInt(7, oram.lengthOfIden)),
							env.inputOfAlice(Utils.fromInt(config.getBobInputSize(), WORD_SIZE)));
				if (config.getBinaryFileName().equals("func_point"))
					oram.write(env.inputOfAlice(Utils.fromInt(7, oram.lengthOfIden)),
							env.inputOfAlice(Utils.fromInt(config.getAliceIntInput(), WORD_SIZE)));
			
			
			env.flush();
			int stackPointer;
			stackPointer = dataOffset - (4*(config.getAliceInputSize() + config.getBobInputSize())) - 32;
			oram.write(env.inputOfAlice(Utils.fromInt(29, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			
			oram.write(env.inputOfAlice(Utils.fromInt(30, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			//global pointer? 
			oram.write(env.inputOfAlice(Utils.fromInt(28, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			
			
			
			return oram;
		}

		private SecureMap<T> loadInstructionsSingleBank(CompEnv<T> env, DataSegment instData)
				throws Exception {
			TreeMap<Long, boolean[]> instructions = null; 
			
			int numInst = instData.getDataLength();
			System.out.println("entering getInstructions, SingleBank.  Size:" + numInst);
			instructions = instData.getDataAsBooleanMap(); 

			if(config.getBinaryFileName().equals("func_point")){
				instructions.putAll(aliceFuncMap);
				numInst += config.getAliceFuncSize();
			}
			
			//once we split the instruction from memory, remove the + MEMORY_SIZE
			SecureMap<T> instBank = new SecureMap<T>(env, numInst, WORD_SIZE, THRESHOLD);
			IntegerLib<T> lib = new IntegerLib<T>(env);
			T[] data; 
			T[] index;

			if (env.getParty() == Party.Alice)
				instBank.init(instructions, 32, 32);
			else
				instBank.init(numInst, 32, 32);
//			for (int i = 0; i < numInst; i++){
//				index = lib.toSignals(i, instBank.lengthOfIden);
//				if (env.getParty() == Party.Alice)
//					data = env.inputOfAlice(instructions[i]);
//				else 
//					data = env.inputOfAlice(new boolean[WORD_SIZE]);
//				instBank.write(index, data);
//				//System.out.println("Wrote instruction number "+i);
//			}		
			//System.out.println("exiting getInstructions");
			return instBank;
		}			

		private void loadInstructionsMultiBanks(CompEnv<T> env, SecureMap<T> singleBank, List<MemorySet<T>> sets) throws Exception {
			//System.out.println("entering loadInstructions");
			IntegerLib<T> lib = new IntegerLib<T>(env);
			T[] data; 
			T[] index;
			SecureMap<T> instructionBank;

			for(MemorySet<T> s:sets) {
				int i = s.getExecutionStep();

				EmulatorUtils.print("step: " + i + " size: " + s.size(), lib);

				TreeMap<Long,boolean[]> m = s.getAddressMap();	  
				long maxAddr = m.lastEntry().getKey();
				if (maxAddr == 0)
					break;
				//long minAddr = m.firstEntry().getKey();
					// do we still need this?
				long minAddr;
				if (s.size() == 1)
					minAddr = maxAddr;
				else minAddr = m.ceilingKey((long)1);
				
				if (!config.isMultipleBanks())
					instructionBank = singleBank;
				else {
					instructionBank = new SecureMap<T>(env, s.size(), WORD_SIZE);
					int count = 0;
					if (config.getMode() == Mode.VERIFY){
						for( Map.Entry<Long, boolean[]> entry : m.entrySet()) {
							if (env.getParty() == Party.Alice) {
								EmulatorUtils.print("count: " + count + " key: " + entry.getKey() +
										" (0x" + Long.toHexString(entry.getKey()) + ")" +
										" value: " , lib, muteLoadInstructions);
								String output = "";
								for (int j = 31 ; j >= 0;  j--){
									if (entry.getValue()[j])
										output += "1";
									else 
										output += "0";
								}
								EmulatorUtils.print(output, lib, muteLoadInstructions);
							}
							count++;
						}
					}

					if (env.getParty() == Party.Alice){
						instructionBank.init(m, 32, 32);
					}
					else 
						instructionBank.init(m.size(), 32, 32);
					
					if (!muteLoadInstructions)
						instructionBank.print();
					//						if (entry.getKey() > 0){
						//							index = lib.toSignals((int)((entry.getKey() - minAddr)/4), instructionBank.lengthOfIden);
						//							if (env.getParty() == Party.Alice){
						//								data = env.inputOfAlice(entry.getValue());
						//							}
						//							else	 { 
						//								data = env.inputOfAlice(new boolean[WORD_SIZE]); 
						//
						//							}
						//							// once the indices are correct, write here. 
						//							instructionBank.write(index, data);
						//						}
						
					

				}
				OramBank<T> bank = new OramBank<T>(instructionBank);
				bank.setMaxAddress(maxAddr);
				bank.setMinAddress(minAddr);
				s.setOramBank(bank);
			}		
			//System.out.println("exiting getInstructions");
		}


		// load Alices code into memory here. (should be setMemory)

		//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
		public SecureArray<T> getMemory(CompEnv<T> env, DataSegment memData) throws Exception{
			//System.out.println("entering getMemoryGen");
			boolean memory[][] = memData.getDataAsBoolean();	
			IntegerLib<T> lib = new IntegerLib<T>(env);
			int dataLen = memData.getDataLength();
			int memSize = stackSize + dataLen;
			SecureArray<T> memBank = new SecureArray<T>(env, memSize, WORD_SIZE, THRESHOLD, RECURSE_THRESHOLD, 4);
			
			T[] index; 
			T[] data;
            T[][] data2D;
			for (int i = 0; i < dataLen; i++){
				index = lib.toSignals(i + stackSize, memBank.lengthOfIden);
				if (env.getParty() == Party.Alice)
					data = env.inputOfAlice(memory[i]);
				else 
					data = env.inputOfAlice(new boolean[WORD_SIZE]);
				memBank.write(index, data);	
			}
			if (config.getBinaryFileName().equals("djikstra")){
				for (int i = 0; i < aliceInput_2D.length; i++){
					for (int j = 0; j < aliceInput_2D[0].length; j++){
						index = lib.toSignals(stackSize - config.getAliceInputSize() + (i * aliceInput_2D[0].length)+j, memBank.lengthOfIden);
						if (env.getParty() == Party.Alice)
							data = env.inputOfAlice(Utils.fromInt(aliceInput_2D[i][j], WORD_SIZE));
						else 
							data = env.inputOfAlice(new boolean[WORD_SIZE]);
						memBank.write(index, data);						
					}
				}
			}
			if (config.getBinaryFileName().equals("set_intersection") || config.getBinaryFileName().equals("lcs") 
					|| config.getBinaryFileName().equals("func_point") ){
				for (int i = 0; i < aliceInputArray.length; i++){
					index = lib.toSignals(stackSize - config.getAliceInputSize() - config.getBobInputSize() + i , memBank.lengthOfIden);
					if (env.getParty() == Party.Alice)
						data = env.inputOfAlice(Utils.fromInt(aliceInputArray[i], WORD_SIZE));
					else 
						data = env.inputOfAlice(new boolean[WORD_SIZE]);
					memBank.write(index, data);					
				}
				for (int i = 0; i < bobInputArray.length; i++){
					index = lib.toSignals(stackSize - config.getBobInputSize() + i , memBank.lengthOfIden);
					if (env.getParty() == Party.Alice)
						data = env.inputOfAlice(Utils.fromInt(bobInputArray[i], WORD_SIZE));
					else 
						data = env.inputOfAlice(new boolean[WORD_SIZE]);
					memBank.write(index, data);					
				}
			}
			if (config.getBinaryFileName().equals("bubble_sort") || config.getBinaryFileName().equals("binary_search")){
				for (int i = 0; i < aliceInputArray.length; i++){
					index = lib.toSignals(stackSize - config.getAliceInputSize() + i , memBank.lengthOfIden);
					if (env.getParty() == Party.Alice)
						data = env.inputOfAlice(Utils.fromInt(aliceInputArray[i], WORD_SIZE));
					else
						data = env.inputOfAlice(new boolean[WORD_SIZE]);
					memBank.write(index, data);
				}
			}

            if (config.getBinaryFileName().equals("func_point")){
                
            }



			EmulatorUtils.printOramBank(memBank, lib, stackSize + dataLen);
			//System.out.println("exiting getMemoryGen");
			
			return memBank;
		}
	}

	private static void process_cmdline_args(String[] args, LocalConfiguration config) {
		CmdLineParser parser = new CmdLineParser();

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}

		// Pick off file name, which should be remaining arg
		// (and currently only arg)
		// If no file name, will get from properties file.
		// This is probably an error, but allow it in the interest
		// of backwards compatibility.

		String rest[] = parser.getRemainingArgs();
		if(rest.length != 1) {
			printUsage();
			System.exit(2);
		}
		config.setBinaryFileName(rest[0]);
	}

	private static void printUsage() {
		System.out.println("Usage: java RunACSEmulatorServer [binary file]");
	}

	static public void main(String args[]) throws Exception {
		// Problem:  We need to parse args and config in order to determine
		// mode (VERIFY or REAL), but we need to stash the filename in the emulator
		// Solution: Subclass Configuration and stash the filename there.  We
		// use this class to store the mode also
		LocalConfiguration config = new LocalConfiguration();
		//process_cmdline_args(args, config);
		MipsEmulator emu = null;
		switch(config.getMode()) {
		case VERIFY:
			emu = new MipsEmulatorImpl<Boolean>(config);
			break;
		case REAL:
			emu = new MipsEmulatorImpl<GCSignal>(config);
			break;
		default:
			System.err.println("Help!  What do I do about " +  config.getMode() + "?");
			System.exit(1);
		}
		emu.emulate();
	}
	
	public void emulate() throws Exception {
		Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
		System.err.println("Executing binary file: " + config.getBinaryFileName());
		System.err.println("Alice input Size: " + config.getAliceInputSize());
		System.err.println("Bob input Size: " + config.getBobInputSize());
		System.err.println("Bob integer input: " + config.getBobIntInput());
		System.err.println("Bob integer input2: " + config.getBobIntInput2());
		System.err.println("Alice integer input: " + config.getAliceIntInput());
		System.err.println("Alice integer input2: " + config.getAliceIntInput2());
		System.err.println("Alice function input: " + config.getAliceFuncInput());
		System.out.println("Executing binary file: " + config.getBinaryFileName());
		System.out.println("Alice input Size: " + config.getAliceInputSize());
		System.out.println("Bob input Size: " + config.getBobInputSize());
		System.out.println("Bob integer input: " + config.getBobIntInput());
		System.out.println("Bob integer input2: " + config.getBobIntInput2());
		System.out.println("Alice integer input: " + config.getAliceIntInput());
		System.out.println("Alice integer input2: " + config.getAliceIntInput2());
		System.out.println("Alice function input: " + config.getAliceFuncInput());
		SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());



		//load sfe_main as input

		// setup sep data segment obj

		// single call to get bool array of instructions

		// load into memory

		// getMemory print statements to see if it loads to memory

		// where to memory to put it, leave to me.

		// set intersection has two arrays

		// alices input in memory, bobs next to it



		


		// function load list is functions we are execuing in program

		// getInstructions goes through and gets the instructions for every function
		// in the load list

		DataSegment instData = rdr.getInstructions(config.getFunctionLoadList());



		// load alices function with same getInstruction into DataSegment

		// get instructions from DataSegment as array

		// create another data input that doesnt include alices input
		// separate data input just for alices input
		// grab alices code in a separte data segment containing instructions

		DataSegment memData = rdr.getData();

		// is this cast ok?  Or should we modify the mem circuit? 
		int pcOffset = (int) ent.getAddress();
		int dataOffset = (int) rdr.getDataAddress();
		System.err.println("dataOffset: " + dataOffset);
		MemSetBuilder<ET> b = new MemSetBuilder<ET>(config, config.getBinaryFileName());
		//List<MemorySet>sets = b.build();
		System.err.println("mode is " + config.getMode());
		GenRunnable<ET> gen = new GenRunnable<ET>(b.build(), instData, memData, pcOffset, dataOffset);
		EvaRunnable<ET> env = new EvaRunnable<ET>(b.build(), instData, memData, pcOffset, dataOffset);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join(); 

	}

	private class GenRunnable<T> extends network.Server implements Runnable {
		MipsParty<T> mips;

		public GenRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				listen(54321);
				// @SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(config.getMode(), Party.Alice, is, os);
				mips.mainloop(env);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	private class EvaRunnable<T> extends network.Client implements Runnable {
		MipsParty<T> mips;

		public EvaRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(config.getMode(), Party.Bob, is, os);
				mips.mainloop(env);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
