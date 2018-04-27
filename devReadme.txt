As it stands, we only have one build/run combo for both single thread and concurrent implementations of Chess. The output file is named in line with the convention that this course's homwork files have employed; e.g., ChessOutput<job-number>.out.

To compare run times, modify the method call in MinMaxPlayer's getNextMove function. Using function decision() invokes the concurrent implementation, while SingleThreadDecision() does as the name implies. A cleaner split of logic should be introduced before signing off on the final product. Both functions expect a Board object as a parameter.

(For clarity, I've been changing the print statement in Chess#main to reflect which version I'm running.)

Before running scripts, you may need to add execution permission:
	chmod +x <script-name>
