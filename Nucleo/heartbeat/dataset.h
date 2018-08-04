const int rows = 300; // number of instances
const int columns = 5; // number of attributes

float instances[rows][columns] = {
  {916.0, 912.0, 924.0, 912.0, 918.8},
  {926.6666667, 1132.0, 708.0, 940.0, 894.8},
  {418.6666667, 472.0, 404.0, 380.0, 444.4},
  {938.6666667, 928.0, 932.0, 956.0, 924.0},
  {685.3333333, 752.0, 652.0, 652.0, 707.6},
  {909.3333333, 892.0, 916.0, 920.0, 892.0},
  {838.6666667, 828.0, 840.0, 848.0, 841.2},
  {881.3333333, 872.0, 884.0, 888.0, 882.8},
  {593.3333333, 748.0, 472.0, 560.0, 538.8},
  {724.0, 736.0, 720.0, 716.0, 722.8},
  {885.3333333, 896.0, 876.0, 884.0, 880.0},
  {942.6666667, 940.0, 936.0, 952.0, 922.8},
  {558.6666667, 512.0, 532.0, 632.0, 528.4},
  {522.6666667, 432.0, 660.0, 476.0, 458.0},
  {448.0, 336.0, 500.0, 508.0, 509.6},
  {509.3333333, 676.0, 440.0, 412.0, 375.2},
  {818.6666667, 852.0, 800.0, 804.0, 799.6},
  {854.6666667, 860.0, 848.0, 856.0, 851.6},
  {313.3333333, 272.0, 312.0, 356.0, 379.6},
  {734.6666667, 616.0, 868.0, 720.0, 871.6},
  {878.6666667, 876.0, 892.0, 868.0, 881.2},
  {389.3333333, 424.0, 360.0, 384.0, 366.0},
  {474.6666667, 428.0, 324.0, 672.0, 430.0},
  {912.0, 916.0, 920.0, 900.0, 912.4},
  {838.6666667, 836.0, 844.0, 836.0, 841.2},
  {453.3333333, 524.0, 500.0, 336.0, 480.8},
  {454.6666667, 356.0, 380.0, 628.0, 423.6},
  {900.0, 896.0, 916.0, 888.0, 908.8},
  {925.3333333, 928.0, 928.0, 920.0, 917.2},
  {454.6666667, 444.0, 468.0, 452.0, 565.6},
  {421.3333333, 364.0, 524.0, 376.0, 526.0},
  {949.3333333, 956.0, 956.0, 936.0, 942.0},
  {844.0, 836.0, 844.0, 852.0, 844.4},
  {477.3333333, 532.0, 416.0, 484.0, 551.6},
  {977.3333333, 972.0, 984.0, 976.0, 928.4},
  {441.3333333, 456.0, 520.0, 348.0, 828.8},
  {872.0, 744.0, 1172.0, 700.0, 708.8},
  {848.0, 880.0, 816.0, 848.0, 852.0},
  {818.6666667, 816.0, 836.0, 804.0, 811.6},
  {754.6666667, 756.0, 764.0, 744.0, 834.8},
  {800.0, 796.0, 804.0, 800.0, 800.0},
  {986.6666667, 984.0, 964.0, 1012.0, 991.6},
  {392.0, 340.0, 552.0, 284.0, 424.4},
  {341.3333333, 328.0, 416.0, 280.0, 346.8},
  {416.0, 356.0, 376.0, 516.0, 556.4},
  {538.6666667, 776.0, 408.0, 432.0, 445.6},
  {682.6666667, 552.0, 840.0, 656.0, 859.2},
  {826.6666667, 832.0, 816.0, 832.0, 828.8},
  {456.0, 592.0, 352.0, 424.0, 395.6},
  {856.0, 768.0, 804.0, 996.0, 754.8},
  {670.6666667, 740.0, 736.0, 536.0, 788.0},
  {886.6666667, 892.0, 900.0, 868.0, 900.4},
  {418.6666667, 392.0, 560.0, 304.0, 398.8},
  {888.0, 896.0, 868.0, 900.0, 891.6},
  {488.0, 392.0, 420.0, 652.0, 446.4},
  {368.0, 460.0, 320.0, 324.0, 478.8},
  {832.0, 828.0, 796.0, 872.0, 831.2},
  {353.3333333, 356.0, 328.0, 376.0, 382.8},
  {804.0, 796.0, 820.0, 796.0, 856.0},
  {952.0, 960.0, 972.0, 924.0, 996.8},
  {840.0, 824.0, 848.0, 848.0, 838.0},
  {1045.333333, 1004.0, 1124.0, 1008.0, 808.8},
  {918.6666667, 920.0, 912.0, 924.0, 915.2},
  {716.0, 708.0, 720.0, 720.0, 710.4},
  {842.6666667, 852.0, 832.0, 844.0, 840.8},
  {498.6666667, 440.0, 304.0, 752.0, 636.8},
  {925.3333333, 920.0, 948.0, 908.0, 922.8},
  {922.6666667, 932.0, 908.0, 928.0, 916.0},
  {838.6666667, 828.0, 844.0, 844.0, 840.8},
  {925.3333333, 936.0, 916.0, 924.0, 951.2},
  {916.0, 920.0, 928.0, 900.0, 902.0},
  {878.6666667, 876.0, 876.0, 884.0, 880.8},
  {476.0, 324.0, 484.0, 620.0, 533.2},
  {414.6666667, 344.0, 464.0, 436.0, 563.6},
  {825.3333333, 820.0, 832.0, 824.0, 825.2},
  {909.3333333, 904.0, 908.0, 916.0, 902.0},
  {888.0, 900.0, 884.0, 880.0, 884.8},
  {918.6666667, 924.0, 908.0, 924.0, 907.2},
  {848.0, 1160.0, 664.0, 720.0, 797.2},
  {854.6666667, 852.0, 852.0, 860.0, 846.4},
  {805.3333333, 812.0, 716.0, 888.0, 806.8},
  {370.6666667, 428.0, 360.0, 324.0, 692.4},
  {836.0, 832.0, 848.0, 828.0, 840.4},
  {872.0, 872.0, 892.0, 852.0, 884.8},
  {401.3333333, 364.0, 384.0, 456.0, 400.4},
  {872.0, 872.0, 888.0, 856.0, 869.6},
  {365.3333333, 280.0, 284.0, 532.0, 488.4},
  {452.0, 648.0, 360.0, 348.0, 390.8},
  {594.6666667, 756.0, 572.0, 456.0, 608.4},
  {917.3333333, 928.0, 912.0, 912.0, 910.4},
  {840.0, 832.0, 860.0, 828.0, 838.8},
  {824.0, 820.0, 824.0, 828.0, 822.0},
  {329.3333333, 332.0, 320.0, 336.0, 482.0},
  {740.0, 612.0, 656.0, 952.0, 806.4},
  {396.0, 452.0, 344.0, 392.0, 482.4},
  {872.0, 864.0, 876.0, 876.0, 871.6},
  {382.6666667, 356.0, 388.0, 404.0, 467.6},
  {885.3333333, 892.0, 880.0, 884.0, 864.4},
  {424.0, 656.0, 284.0, 332.0, 416.8},
  {424.0, 340.0, 396.0, 536.0, 494.8},
  {421.3333333, 544.0, 348.0, 372.0, 458.4},
  {842.6666667, 956.0, 976.0, 596.0, 774.4},
  {426.6666667, 448.0, 356.0, 476.0, 442.0},
  {518.6666667, 752.0, 440.0, 364.0, 605.6},
  {830.6666667, 832.0, 852.0, 808.0, 891.6},
  {916.0, 924.0, 916.0, 908.0, 916.4},
  {962.6666667, 940.0, 1008.0, 940.0, 943.6},
  {422.6666667, 372.0, 408.0, 488.0, 721.2},
  {473.3333333, 628.0, 288.0, 504.0, 430.4},
  {1189.333333, 1132.0, 1220.0, 1216.0, 1190.4},
  {886.6666667, 688.0, 1112.0, 860.0, 848.8},
  {356.0, 348.0, 408.0, 312.0, 340.0},
  {752.0, 996.0, 768.0, 492.0, 743.2},
  {844.0, 852.0, 840.0, 840.0, 841.2},
  {425.3333333, 408.0, 492.0, 376.0, 465.6},
  {981.3333333, 980.0, 992.0, 972.0, 990.4},
  {505.3333333, 420.0, 516.0, 580.0, 437.2},
  {349.3333333, 412.0, 284.0, 352.0, 390.8},
  {366.6666667, 332.0, 408.0, 360.0, 398.8},
  {754.6666667, 724.0, 756.0, 784.0, 752.8},
  {774.6666667, 584.0, 968.0, 772.0, 738.0},
  {813.3333333, 816.0, 816.0, 808.0, 818.4},
  {938.6666667, 944.0, 928.0, 944.0, 919.6},
  {369.3333333, 392.0, 428.0, 288.0, 452.8},
  {897.3333333, 884.0, 888.0, 920.0, 845.6},
  {872.0, 880.0, 872.0, 864.0, 870.4},
  {838.6666667, 844.0, 836.0, 836.0, 840.0},
  {824.0, 820.0, 820.0, 832.0, 828.4},
  {908.0, 904.0, 928.0, 892.0, 915.2},
  {400.0, 492.0, 344.0, 364.0, 433.2},
  {466.6666667, 348.0, 400.0, 652.0, 379.2},
  {916.0, 904.0, 932.0, 912.0, 910.0},
  {706.6666667, 952.0, 656.0, 512.0, 676.8},
  {530.6666667, 384.0, 468.0, 740.0, 458.0},
  {500.0, 476.0, 488.0, 536.0, 479.6},
  {818.6666667, 940.0, 720.0, 796.0, 829.2},
  {762.6666667, 800.0, 608.0, 880.0, 743.6},
  {834.6666667, 792.0, 832.0, 880.0, 763.6},
  {857.3333333, 860.0, 872.0, 840.0, 859.6},
  {929.3333333, 912.0, 948.0, 928.0, 930.4},
  {533.3333333, 564.0, 608.0, 428.0, 420.0},
  {434.6666667, 312.0, 692.0, 300.0, 476.8},
  {884.0, 876.0, 880.0, 896.0, 882.8},
  {360.0, 328.0, 316.0, 436.0, 537.2},
  {796.0, 1152.0, 340.0, 896.0, 415.6},
  {832.0, 832.0, 828.0, 836.0, 838.4},
  {800.0, 796.0, 940.0, 664.0, 818.4},
  {844.0, 844.0, 848.0, 840.0, 853.6},
  {845.3333333, 848.0, 848.0, 840.0, 846.8},
  {445.3333333, 456.0, 356.0, 524.0, 430.4},
  {394.6666667, 416.0, 380.0, 388.0, 401.6},
  {428.0, 444.0, 536.0, 304.0, 492.8},
  {862.6666667, 860.0, 852.0, 876.0, 849.2},
  {645.3333333, 548.0, 888.0, 500.0, 836.0},
  {485.3333333, 620.0, 324.0, 512.0, 481.6},
  {921.3333333, 920.0, 920.0, 924.0, 964.0},
  {422.6666667, 340.0, 524.0, 404.0, 403.2},
  {833.3333333, 832.0, 836.0, 832.0, 861.2},
  {377.3333333, 352.0, 408.0, 372.0, 736.8},
  {553.3333333, 632.0, 512.0, 516.0, 517.6},
  {928.0, 932.0, 944.0, 908.0, 926.0},
  {940.0, 948.0, 940.0, 932.0, 945.6},
  {901.3333333, 896.0, 920.0, 888.0, 901.6},
  {674.6666667, 588.0, 492.0, 944.0, 454.4},
  {737.3333333, 632.0, 912.0, 668.0, 744.8},
  {938.6666667, 952.0, 940.0, 924.0, 938.4},
  {828.0, 824.0, 828.0, 832.0, 825.6},
  {886.6666667, 888.0, 884.0, 888.0, 882.8},
  {930.6666667, 916.0, 944.0, 932.0, 916.4},
  {500.0, 480.0, 444.0, 576.0, 532.8},
  {325.3333333, 332.0, 348.0, 296.0, 384.8},
  {493.3333333, 352.0, 392.0, 736.0, 425.2},
  {388.0, 408.0, 352.0, 404.0, 365.2},
  {880.0, 888.0, 880.0, 872.0, 846.8},
  {713.3333333, 760.0, 408.0, 972.0, 789.2},
  {925.3333333, 948.0, 920.0, 908.0, 882.8},
  {412.0, 444.0, 348.0, 444.0, 573.2},
  {828.0, 788.0, 832.0, 864.0, 827.6},
  {896.0, 892.0, 904.0, 892.0, 907.2},
  {784.0, 1104.0, 664.0, 584.0, 681.2},
  {370.6666667, 376.0, 352.0, 384.0, 405.2},
  {801.3333333, 800.0, 800.0, 804.0, 792.8},
  {886.6666667, 896.0, 888.0, 876.0, 731.2},
  {416.0, 340.0, 436.0, 472.0, 457.6},
  {718.6666667, 720.0, 720.0, 716.0, 717.6},
  {909.3333333, 912.0, 908.0, 908.0, 908.0},
  {885.3333333, 884.0, 892.0, 880.0, 877.2},
  {484.0, 440.0, 336.0, 676.0, 393.2},
  {416.0, 448.0, 484.0, 316.0, 385.6},
  {805.3333333, 804.0, 800.0, 812.0, 800.8},
  {872.0, 880.0, 868.0, 868.0, 894.0},
  {822.6666667, 836.0, 796.0, 836.0, 824.8},
  {756.0, 752.0, 756.0, 760.0, 764.8},
  {397.3333333, 376.0, 356.0, 460.0, 376.0},
  {441.3333333, 364.0, 420.0, 540.0, 462.4},
  {858.6666667, 820.0, 888.0, 868.0, 854.8},
  {486.6666667, 520.0, 360.0, 580.0, 452.8},
  {956.0, 952.0, 956.0, 960.0, 964.0},
  {504.0, 368.0, 692.0, 452.0, 650.0},
  {629.3333333, 696.0, 656.0, 536.0, 507.2},
  {422.6666667, 616.0, 304.0, 348.0, 324.4},
  {350.6666667, 376.0, 364.0, 312.0, 486.4},
  {914.6666667, 916.0, 912.0, 916.0, 893.2},
  {450.6666667, 408.0, 372.0, 572.0, 658.8},
  {428.0, 396.0, 516.0, 372.0, 462.4},
  {364.0, 312.0, 280.0, 500.0, 399.6},
  {502.6666667, 344.0, 696.0, 468.0, 442.8},
  {964.0, 964.0, 948.0, 980.0, 960.4},
  {910.6666667, 912.0, 904.0, 916.0, 910.4},
  {373.3333333, 444.0, 280.0, 396.0, 367.2},
  {758.6666667, 760.0, 760.0, 756.0, 766.4},
  {353.3333333, 412.0, 280.0, 368.0, 390.4},
  {796.0, 680.0, 808.0, 900.0, 858.0},
  {348.0, 292.0, 344.0, 408.0, 368.8},
  {420.0, 372.0, 404.0, 484.0, 460.0},
  {878.6666667, 880.0, 888.0, 868.0, 862.8},
  {850.6666667, 864.0, 840.0, 848.0, 840.8},
  {882.6666667, 876.0, 900.0, 872.0, 899.2},
  {826.6666667, 816.0, 836.0, 828.0, 834.4},
  {836.0, 836.0, 840.0, 832.0, 843.2},
  {846.6666667, 852.0, 864.0, 824.0, 806.8},
  {376.0, 372.0, 396.0, 360.0, 444.4},
  {660.0, 756.0, 500.0, 724.0, 632.0},
  {834.6666667, 808.0, 836.0, 860.0, 833.6},
  {465.3333333, 460.0, 376.0, 560.0, 378.4},
  {973.3333333, 1044.0, 1036.0, 840.0, 1014.0},
  {934.6666667, 916.0, 960.0, 928.0, 934.4},
  {780.0, 776.0, 780.0, 784.0, 785.2},
  {910.6666667, 904.0, 908.0, 920.0, 884.8},
  {906.6666667, 920.0, 900.0, 900.0, 904.0},
  {481.3333333, 380.0, 700.0, 364.0, 501.6},
  {845.3333333, 840.0, 852.0, 844.0, 890.8},
  {553.3333333, 544.0, 516.0, 600.0, 395.2},
  {726.6666667, 708.0, 812.0, 660.0, 864.4},
  {792.0, 620.0, 780.0, 976.0, 790.0},
  {438.6666667, 516.0, 380.0, 420.0, 436.4},
  {472.0, 460.0, 472.0, 484.0, 495.2},
  {329.3333333, 312.0, 324.0, 352.0, 416.4},
  {777.3333333, 1156.0, 328.0, 848.0, 480.0},
  {882.6666667, 896.0, 940.0, 812.0, 920.4},
  {470.6666667, 360.0, 424.0, 628.0, 372.0},
  {452.0, 484.0, 372.0, 500.0, 440.4},
  {836.0, 864.0, 804.0, 840.0, 839.2},
  {425.3333333, 420.0, 464.0, 392.0, 462.0},
  {872.0, 780.0, 904.0, 932.0, 903.6},
  {553.3333333, 580.0, 520.0, 560.0, 459.6},
  {838.6666667, 848.0, 840.0, 828.0, 844.0},
  {932.0, 936.0, 928.0, 932.0, 922.4},
  {438.6666667, 388.0, 516.0, 412.0, 729.2},
  {832.0, 824.0, 832.0, 840.0, 844.0},
  {853.3333333, 964.0, 756.0, 840.0, 894.4},
  {1090.666667, 1080.0, 1100.0, 1092.0, 1050.4},
  {916.0, 916.0, 912.0, 920.0, 915.2},
  {880.0, 892.0, 884.0, 864.0, 914.0},
  {501.3333333, 616.0, 528.0, 360.0, 509.2},
  {878.6666667, 888.0, 872.0, 876.0, 878.0},
  {393.3333333, 348.0, 412.0, 420.0, 493.2},
  {389.3333333, 300.0, 312.0, 556.0, 354.8},
  {860.0, 864.0, 844.0, 872.0, 846.4},
  {960.0, 968.0, 940.0, 972.0, 952.4},
  {861.3333333, 856.0, 864.0, 864.0, 870.0},
  {518.6666667, 472.0, 516.0, 568.0, 442.0},
  {834.6666667, 836.0, 836.0, 832.0, 841.2},
  {913.3333333, 920.0, 900.0, 920.0, 910.8},
  {806.6666667, 808.0, 804.0, 808.0, 982.0},
  {670.6666667, 672.0, 672.0, 668.0, 665.2},
  {933.3333333, 936.0, 940.0, 924.0, 931.6},
  {332.0, 284.0, 388.0, 324.0, 423.2},
  {841.3333333, 836.0, 844.0, 844.0, 847.2},
  {393.3333333, 592.0, 308.0, 280.0, 387.6},
  {1101.333333, 908.0, 952.0, 1444.0, 807.6},
  {817.3333333, 820.0, 812.0, 820.0, 817.2},
  {720.0, 716.0, 720.0, 724.0, 720.8},
  {801.3333333, 840.0, 1044.0, 520.0, 1037.2},
  {530.6666667, 680.0, 452.0, 460.0, 410.8},
  {552.0, 424.0, 744.0, 488.0, 494.4},
  {840.0, 848.0, 856.0, 816.0, 852.0},
  {380.0, 344.0, 384.0, 412.0, 415.6},
  {470.6666667, 324.0, 776.0, 312.0, 425.6},
  {858.6666667, 976.0, 620.0, 980.0, 761.2},
  {849.3333333, 848.0, 844.0, 856.0, 869.2},
  {440.0, 396.0, 556.0, 368.0, 432.4},
  {904.0, 908.0, 912.0, 892.0, 932.8},
  {876.0, 900.0, 864.0, 864.0, 863.2},
  {433.3333333, 520.0, 360.0, 420.0, 427.6},
  {829.3333333, 828.0, 828.0, 832.0, 834.8},
  {846.6666667, 856.0, 840.0, 844.0, 857.6},
  {889.3333333, 880.0, 924.0, 864.0, 894.0},
  {892.0, 904.0, 896.0, 876.0, 890.4},
  {945.3333333, 952.0, 944.0, 940.0, 934.0},
  {377.3333333, 384.0, 376.0, 372.0, 398.8},
  {446.6666667, 504.0, 340.0, 496.0, 422.8},
  {370.6666667, 324.0, 332.0, 456.0, 432.0},
  {636.0, 396.0, 432.0, 1080.0, 653.2},
  {866.6666667, 868.0, 864.0, 868.0, 897.2},
  {442.6666667, 468.0, 448.0, 412.0, 438.4},
  {336.0, 296.0, 332.0, 380.0, 352.8},
  {330.6666667, 304.0, 356.0, 332.0, 382.0},
  {968.0, 964.0, 980.0, 960.0, 957.2},
  {344.0, 328.0, 348.0, 356.0, 394.8}
};

int classes[rows] = {  //outputs of the target function 
  0,
  0,
  1,
  0,
  1,
  0,
  0,
  0,
  1,
  0,
  0,
  0,
  1,
  1,
  1,
  1,
  0,
  0,
  1,
  1,
  0,
  1,
  1,
  0,
  0,
  1,
  1,
  0,
  0,
  1,
  1,
  0,
  0,
  1,
  0,
  1,
  1,
  0,
  0,
  0,
  0,
  0,
  1,
  1,
  1,
  1,
  1,
  0,
  1,
  1,
  1,
  0,
  1,
  0,
  1,
  1,
  0,
  1,
  0,
  0,
  0,
  1,
  0,
  0,
  0,
  1,
  0,
  0,
  0,
  0,
  0,
  0,
  1,
  1,
  0,
  0,
  0,
  0,
  1,
  0,
  1,
  1,
  0,
  0,
  1,
  0,
  1,
  1,
  1,
  0,
  0,
  0,
  1,
  1,
  1,
  0,
  1,
  0,
  1,
  1,
  1,
  1,
  1,
  1,
  0,
  0,
  0,
  1,
  1,
  0,
  1,
  1,
  1,
  0,
  1,
  0,
  1,
  1,
  1,
  0,
  1,
  0,
  0,
  1,
  0,
  0,
  0,
  0,
  0,
  1,
  1,
  0,
  1,
  1,
  1,
  1,
  0,
  1,
  0,
  0,
  1,
  1,
  0,
  1,
  1,
  0,
  1,
  0,
  0,
  1,
  1,
  1,
  0,
  1,
  1,
  0,
  1,
  0,
  1,
  1,
  0,
  0,
  0,
  1,
  1,
  0,
  0,
  0,
  0,
  1,
  1,
  1,
  1,
  0,
  0,
  0,
  1,
  0,
  0,
  1,
  1,
  0,
  0,
  1,
  0,
  0,
  0,
  1,
  1,
  0,
  0,
  0,
  0,
  1,
  1,
  0,
  1,
  0,
  1,
  1,
  1,
  1,
  0,
  1,
  1,
  0,
  1,
  0,
  0,
  1,
  0,
  1,
  1,
  1,
  1,
  0,
  0,
  0,
  0,
  0,
  0,
  1,
  1,
  0,
  1,
  1,
  0,
  0,
  0,
  0,
  1,
  0,
  1,
  1,
  1,
  1,
  1,
  1,
  1,
  0,
  1,
  1,
  0,
  1,
  0,
  1,
  0,
  0,
  1,
  0,
  1,
  0,
  0,
  0,
  1,
  0,
  1,
  1,
  0,
  0,
  0,
  1,
  0,
  0,
  0,
  0,
  0,
  1,
  0,
  1,
  1,
  0,
  0,
  1,
  1,
  1,
  0,
  1,
  1,
  1,
  0,
  1,
  0,
  0,
  1,
  0,
  0,
  0,
  0,
  0,
  1,
  1,
  1,
  1,
  0,
  1,
  1,
  1,
  0,
  1
};
