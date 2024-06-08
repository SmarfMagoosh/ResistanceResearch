import pygambit
from numpy import matmul as mul


#calculate equilibriums for each combination of reports
for i in range(1, 4):    
    # read payoff matrix from file
    payoff = eval(open(f"{i}.txt").read())

    # initialize the game
    game = pygambit.Game.new_table([len(payoff), len(payoff[0])])
    game.title = "Avalon"
    game.players[0].label = "The Strategist"
    game.players[1].label = "The Puppetmaster"

    # set the game's payoff matrix
    for i in range(len(payoff)):
        for j in range(len(payoff[0])):
            game[i, j]["The Strategist"] = payoff[i][j]
            game[i, j]["The Puppetmaster"] = payoff[i][j]
            
    # get strategy vectors for each player
    strats = game.mixed_strategy_profile()
    strategist_strategy = []
    puppetmaster_strategy = []
    for strat in strats:
        if strat[0].player.label == "The Strategist":
            strategist_strategy.append(strat[1])
        else:
            puppetmaster_strategy.append(strat[1])
    
    print(strategist_strategy)
    print(puppetmaster_strategy)
    equilibrium = mul(mul(strategist_strategy, payoff), puppetmaster_strategy)
    # print(equilibrium)