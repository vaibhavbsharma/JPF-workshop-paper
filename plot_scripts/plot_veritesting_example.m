normal_execution_paths = [ 19 42 76 123 185 264 362 481 623 790 984 1207 1461 1748 2070 2429 2827 3266 3748 4275 4849 5472 6146 6873 7655];
veritesting_execution_paths = 3:27;

x_vals=3:2:51;

hold on;

plot(x_vals,log10(normal_execution_paths),'r--','Linewidth',4);

plot(x_vals,log10(veritesting_execution_paths),'b-.','Linewidth',4);


set(gca,'FontSize',15); 
h_legend=legend('Vanilla SPF','SPF with Veritesting','Location','northwest');
xlabel('allowed range of symbolic integers');
ylabel('log (number of execution paths)');
xlim([3 51]);
%ylim([0 20]);
set(gca,'FontSize',15);

toplot_legend_loc('allowed range of symbolic integers','log (number of execution paths)','',h_legend,'northwest');  

hold off;

print -depsc '../figures/veritesting_example'
